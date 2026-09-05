/*
 * Java cvs client library package.
 * Copyright (c) 1997 by Timothy Gerard Endres
 *
 * This program is free software.
 *
 * You may redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation.
 * Version 2 of the license should be included with this distribution in
 * the file LICENSE, as well as License.html.
 */

package com.ice.cvsc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


/**
 * Represents and parses an RCS (,v) file.
 * Provides access to revision descriptors, metadata, and reconstructed file contents.
 */
public class RCSFile {

    public static class Revision {
        public String revision;
        public Date date;
        public String author;
        public String state;
        public List<String> branches = new ArrayList<>();
        public String next;
        public String log;
        public byte[] text;
    }

    private static class Token {
        final int type; // 1: ID, 2: STRING (@...@), 3: SEMI/COLON
        final String sval;
        final byte[] bval;

        Token(int type, String sval, byte[] bval) {
            this.type = type;
            this.sval = sval;
            this.bval = bval;
        }
    }

    private String head;
    private String branch;
    private String comment;
    private String expand;
    private final Map<String, String> symbols = new HashMap<>();
    private final Map<String, Revision> revisions = new HashMap<>();
    private String desc;

    public RCSFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = fis.readAllBytes();
            parse(data);
        }
    }

    public RCSFile(InputStream in) throws IOException {
        byte[] data = in.readAllBytes();
        parse(data);
    }

    public RCSFile(byte[] data) throws IOException {
        parse(data);
    }

    private static Token nextToken(byte[] data, int[] pos) {
        int p = pos[0];
        int len = data.length;

        while (p < len && (data[p] == ' ' || data[p] == '\t' || data[p] == '\r' || data[p] == '\n')) {
            p++;
        }
        if (p >= len) {
            pos[0] = p;
            return null;
        }

        byte b = data[p];
        if (b == '@') {
            p++;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (p < len) {
                byte ch = data[p];
                if (ch == '@') {
                    if (p + 1 < len && data[p + 1] == '@') {
                        baos.write('@');
                        p += 2;
                    } else {
                        p++;
                        break;
                    }
                } else {
                    baos.write(ch);
                    p++;
                }
            }
            pos[0] = p;
            byte[] bytes = baos.toByteArray();
            return new Token(2, new String(bytes, StandardCharsets.ISO_8859_1), bytes);
        } else if (b == ';' || b == ':') {
            p++;
            pos[0] = p;
            return new Token(3, String.valueOf((char) b), null);
        } else {
            int start = p;
            while (p < len && data[p] != ' ' && data[p] != '\t' && data[p] != '\r' && data[p] != '\n' && data[p] != ';' && data[p] != ':' && data[p] != '@') {
                p++;
            }
            pos[0] = p;
            String val = new String(data, start, p - start, StandardCharsets.ISO_8859_1);
            return new Token(1, val, null);
        }
    }

    private void parse(byte[] data) throws IOException {
        int[] pos = new int[] { 0 };
        SimpleDateFormat df4 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.US);
        df4.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat df2 = new SimpleDateFormat("yy.MM.dd.HH.mm.ss", Locale.US);
        df2.setTimeZone(TimeZone.getTimeZone("GMT"));

        Revision curRev = null;
        while (true) {
            Token tok = nextToken(data, pos);
            if (tok == null) break;

            if (tok.type == 1) {
                String val = tok.sval;
                if ("head".equals(val)) {
                    Token next = nextToken(data, pos);
                    if (next != null && next.type == 1) {
                        this.head = next.sval;
                    }
                } else if ("branch".equals(val)) {
                    Token next = nextToken(data, pos);
                    if (next != null && next.type == 1) {
                        this.branch = next.sval;
                    }
                } else if ("comment".equals(val)) {
                    Token next = nextToken(data, pos);
                    if (next != null && next.type == 2) {
                        this.comment = next.sval;
                    }
                } else if ("expand".equals(val)) {
                    Token next = nextToken(data, pos);
                    if (next != null && next.type == 2) {
                        this.expand = next.sval;
                    }
                } else if ("symbols".equals(val)) {
                    while (true) {
                        Token symTok = nextToken(data, pos);
                        if (symTok == null || (symTok.type == 3 && ";".equals(symTok.sval))) break;
                        if (symTok.type == 1) {
                            Token colonTok = nextToken(data, pos);
                            if (colonTok != null && colonTok.type == 3 && ":".equals(colonTok.sval)) {
                                Token revTok = nextToken(data, pos);
                                if (revTok != null && revTok.type == 1) {
                                    this.symbols.put(symTok.sval, revTok.sval);
                                }
                            }
                        }
                    }
                } else if ("desc".equals(val)) {
                    Token descTok = nextToken(data, pos);
                    if (descTok != null && descTok.type == 2) {
                        this.desc = descTok.sval;
                    }
                    break;
                } else if (!val.isEmpty() && Character.isDigit(val.charAt(0))) {
                    curRev = new Revision();
                    curRev.revision = val;
                    this.revisions.put(val, curRev);
                } else if ("date".equals(val) && curRev != null) {
                    Token dateTok = nextToken(data, pos);
                    if (dateTok != null && dateTok.type == 1) {
                        try {
                            if (dateTok.sval.indexOf('.') == 4) {
                                curRev.date = df4.parse(dateTok.sval);
                            } else {
                                curRev.date = df2.parse(dateTok.sval);
                            }
                        } catch (ParseException e) {
                            // ignore parse exception
                        }
                    }
                } else if ("author".equals(val) && curRev != null) {
                    Token authTok = nextToken(data, pos);
                    if (authTok != null && authTok.type == 1) {
                        curRev.author = authTok.sval;
                    }
                } else if ("state".equals(val) && curRev != null) {
                    Token stateTok = nextToken(data, pos);
                    if (stateTok != null && stateTok.type == 1) {
                        curRev.state = stateTok.sval;
                    }
                } else if ("branches".equals(val) && curRev != null) {
                    while (true) {
                        Token bTok = nextToken(data, pos);
                        if (bTok == null || (bTok.type == 3 && ";".equals(bTok.sval))) break;
                        if (bTok.type == 1) {
                            curRev.branches.add(bTok.sval);
                        }
                    }
                } else if ("next".equals(val) && curRev != null) {
                    Token nextTok = nextToken(data, pos);
                    if (nextTok != null && nextTok.type == 1) {
                        curRev.next = nextTok.sval;
                    }
                }
            }
        }

        while (true) {
            Token tok = nextToken(data, pos);
            if (tok == null) break;

            if (tok.type == 1 && !tok.sval.isEmpty() && Character.isDigit(tok.sval.charAt(0))) {
                String revNum = tok.sval;
                Revision rev = this.revisions.get(revNum);
                if (rev == null) {
                    rev = new Revision();
                    rev.revision = revNum;
                    this.revisions.put(revNum, rev);
                }

                Token logKw = nextToken(data, pos);
                if (logKw != null && "log".equals(logKw.sval)) {
                    Token logStr = nextToken(data, pos);
                    if (logStr != null) {
                        rev.log = logStr.sval;
                    }
                }

                Token textKw = nextToken(data, pos);
                if (textKw != null && "text".equals(textKw.sval)) {
                    Token textStr = nextToken(data, pos);
                    if (textStr != null) {
                        rev.text = textStr.bval;
                    }
                }
            }
        }
    }

    public String getHeadRevision() {
        return head;
    }

    public String getBranch() {
        return branch;
    }

    public String getComment() {
        return comment;
    }

    public String getExpand() {
        return expand;
    }

    public boolean isBinary() {
        return "b".equals(expand) || "o".equals(expand);
    }

    public Map<String, String> getSymbols() {
        return symbols;
    }

    public Map<String, Revision> getRevisions() {
        return revisions;
    }

    public Revision getRevision(String rev) {
        return revisions.get(rev);
    }

    public String getState() {
        return getState(head);
    }

    public String getState(String rev) {
        Revision r = revisions.get(rev);
        return r != null ? r.state : null;
    }

    public Date getDate() {
        return getDate(head);
    }

    public Date getDate(String rev) {
        Revision r = revisions.get(rev);
        return r != null ? r.date : null;
    }

    public String getAuthor() {
        return getAuthor(head);
    }

    public String getAuthor(String rev) {
        Revision r = revisions.get(rev);
        return r != null ? r.author : null;
    }

    public String getLog() {
        return getLog(head);
    }

    public String getLog(String rev) {
        Revision r = revisions.get(rev);
        return r != null ? r.log : null;
    }

    public byte[] getContent() {
        return getContent(head);
    }

    public byte[] getContent(String revNum) {
        if (revNum == null) revNum = this.head;
        if (revNum == null) return new byte[0];

        Revision rev = this.revisions.get(revNum);
        if (rev == null) return new byte[0];

        if (revNum.equals(this.head)) {
            return rev.text != null ? rev.text : new byte[0];
        }

        // Apply diffs along trunk path from head to revNum
        List<Revision> chain = new ArrayList<>();
        Revision curr = this.revisions.get(this.head);
        while (curr != null) {
            chain.add(curr);
            if (revNum.equals(curr.revision)) break;
            curr = curr.next != null ? this.revisions.get(curr.next) : null;
        }

        if (chain.isEmpty() || !revNum.equals(chain.get(chain.size() - 1).revision)) {
            // Not on trunk or path not found; fallback to rev.text
            return rev.text != null ? rev.text : new byte[0];
        }

        byte[] currentText = chain.get(0).text;
        if (currentText == null) currentText = new byte[0];

        for (int i = 1; i < chain.size(); i++) {
            Revision nextRev = chain.get(i);
            if (nextRev.text != null && nextRev.text.length > 0) {
                currentText = applyRCSDiff(currentText, nextRev.text);
            }
        }

        return currentText;
    }

    private static byte[] applyRCSDiff(byte[] base, byte[] diff) {
        List<String> baseLines = splitLines(new String(base, StandardCharsets.ISO_8859_1));
        String diffStr = new String(diff, StandardCharsets.ISO_8859_1);
        List<String> diffLines = splitLines(diffStr);

        int diffIdx = 0;
        int diffLen = diffLines.size();

        while (diffIdx < diffLen) {
            String cmdLine = diffLines.get(diffIdx++).trim();
            if (cmdLine.isEmpty()) continue;

            char cmd = cmdLine.charAt(0);
            String[] parts = cmdLine.substring(1).trim().split("\\s+");
            if (parts.length < 2) continue;

            int lineNum = Integer.parseInt(parts[0]);
            int count = Integer.parseInt(parts[1]);

            if (cmd == 'd') {
                // Delete count lines starting at lineNum (1-indexed)
                int start = lineNum - 1;
                for (int c = 0; c < count && start < baseLines.size(); c++) {
                    baseLines.remove(start);
                }
            } else if (cmd == 'a') {
                // Insert count lines after lineNum (1-indexed, 0 = before first line)
                int insertPos = lineNum;
                for (int c = 0; c < count && diffIdx < diffLen; c++) {
                    baseLines.add(insertPos++, diffLines.get(diffIdx++));
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String line : baseLines) {
            sb.append(line).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                int end = (i > start && text.charAt(i - 1) == '\r') ? i - 1 : i;
                lines.add(text.substring(start, end));
                start = i + 1;
            }
        }
        if (start < len) {
            lines.add(text.substring(start));
        }
        return lines;
    }
}
