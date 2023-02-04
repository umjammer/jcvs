package com.ice.jcvsii;

import java.util.ArrayList;
import java.util.List;

import com.ice.util.StringUtilities;

public class HTMLHelper {

    public static StringBuilder generateHTMLDiff(StringBuilder buf, String rawDiff, String fileName, String rev1, String rev2) {
        String lnSep = "\r\n";

        String lgdBgColor = "#FF6200";
        String lgdTitleColor = "#FFFFFF";

        String tableColor = "#F0F0F0";
        String titleColor = "#C0C0F0";
        String revHdrColor = "#E0E0E0";
        String diffHdrColor = "#99CCCC";

        String diffColorAdd = "#CCCCFF";
        String diffColorChg = "#99FF99";
        String diffColorRem = "#FFCCCC";
        String diffColorNil = "#CCCCCC";

        String codeFontBeg = "<font face=\"Helvetica,Arial\" size=\"-1\">";
        String codeFontEnd = "</font>";

        // TODO
        // REVIEW
        // I prefer a loop that processed lines individually. This
        // is very wasteful of memory!
        //
        String[] lines = StringUtilities.splitString(rawDiff, "\n");

        int lnIdx = 0;
        int numLines = lines.length;

        for (; lnIdx < numLines; ++lnIdx) {
            if (lines[lnIdx].startsWith("diff ")) {
                ++lnIdx;
                break;
            }
        }

        // TODO This is sophomoric!
        if (lnIdx >= numLines) {
            buf.append("<h3>No Differences</h3>").append(lnSep);
            return buf;
        }

        //
        // START DIFF TABLE
        //
        buf.append("<table bgcolor=\"");
        buf.append(tableColor);
        buf.append("\" width=\"100%\" border=0");
        buf.append(" cellspacing=0 cellpadding=0>");
        buf.append(lnSep);

        //
        // TITLE CELL
        //
        buf.append("<tr bgcolor=\"");
        buf.append(titleColor);
        buf.append("\">");
        buf.append(lnSep);

        buf.append("<td align=\"center\" colspan=2>").append(lnSep);

        buf.append("<table width=\"100%\" border=1 cellpadding=3>").append(lnSep);

        buf.append("<tr>").append(lnSep);

        buf.append("<td align=center colspan=2>").append(lnSep);
        buf.append("<font size=\"+1\">").append(lnSep);
        buf.append("<b>").append(lnSep);
        buf.append("<a href=\"#RAW\">Diff</a>&nbsp;of ");
        buf.append(fileName);
        buf.append("</b>").append(lnSep);
        buf.append("</font>").append(lnSep);
        buf.append("</td>").append(lnSep);

        buf.append("</tr>").append(lnSep);

        buf.append("<tr bgcolor=\"");
        buf.append(revHdrColor);
        buf.append("\">").append(lnSep);

        buf.append("<th align=center width=\"50%\">").append(lnSep);
        buf.append("<font size=\"+1\">").append(lnSep);
        buf.append("<b>").append(lnSep);
        buf.append("Version&nbsp;");
        buf.append(rev1);
        buf.append("</b>").append(lnSep);
        buf.append("</font>").append(lnSep);
        buf.append("</th>").append(lnSep);

        buf.append("<th align=center>").append(lnSep);
        buf.append("<font size=\"+1\">").append(lnSep);
        buf.append("<b>").append(lnSep);
        buf.append("Version&nbsp;");
        buf.append(rev2);
        buf.append("</b>").append(lnSep);
        buf.append("</font>").append(lnSep);
        buf.append("</th>").append(lnSep);

        buf.append("</tr>").append(lnSep);

        buf.append("</table>").append(lnSep);

        buf.append("</td>").append(lnSep);

        buf.append("</tr>").append(lnSep);

        //
        // DIFFS
        //
        char state = 'D';

        List<String> ltColV = new ArrayList<>();
        List<String> rtColV = new ArrayList<>();

        for (; lnIdx < numLines; ++lnIdx) {
            String ln = lines[lnIdx];

            if (ln.startsWith("@@")) {
                String[] flds;

                flds = StringUtilities.splitString(ln, " ");

                String oldStr = flds[1].substring(1);
                String newStr = flds[2].substring(1);

                flds = StringUtilities.splitString(oldStr, ",");
                String oldLineCnt = "";
                String oldLineNum = flds[0];
                if (flds.length > 1) oldLineCnt = flds[1];

                flds = StringUtilities.splitString(newStr, ",");
                String newLineCnt = "1";
                String newLineNum = flds[0];
                if (flds.length > 1) newLineCnt = flds[1];

                buf.append("<tr bgcolor=\"");
                buf.append(diffHdrColor);
                buf.append("\">");
                buf.append(lnSep);

                buf.append("<td width=\"50%\">").append(lnSep);

                buf.append("<table width=\"100%\" border=1 cellpadding=3>").append(lnSep);
                buf.append("<tr>").append(lnSep);
                buf.append("<td>").append(lnSep);
                buf.append("<b>Line&nbsp;");
                buf.append(oldLineNum);
                buf.append("</b>").append(lnSep);
                buf.append("</td>").append(lnSep);
                buf.append("</tr>").append(lnSep);
                buf.append("</table>").append(lnSep);

                buf.append("</td>").append(lnSep);

                buf.append("<td width=\"50%\">").append(lnSep);

                buf.append("<table width=100% border=1 cellpadding=3>").append(lnSep);
                buf.append("<tr>").append(lnSep);
                buf.append("<td>").append(lnSep);
                buf.append("<b>Line&nbsp;");
                buf.append(newLineNum);
                buf.append("</b>").append(lnSep);
                buf.append("</td>").append(lnSep);
                buf.append("</tr>").append(lnSep);
                buf.append("</table>").append(lnSep);

                buf.append("</td>").append(lnSep);
                buf.append("</tr>").append(lnSep);

                state = 'D'; // DUMPing...
                ltColV.clear();
                rtColV.clear();
            } else {
                char diffCode = ln.charAt(0);
                String remStr = HTMLHelper.escapeHTML(ln.substring(1));

                //########
                // ZZ
                // (Hen, zeller@think.de)
                // little state machine to parse unified-diff output
                // in order to get some nice 'ediff'-mode output
                // states:
                //  D "dump"             - just dump the value
                //  R "PreChangeRemove"  - we began with '-' .. so this could be the start of a 'change' area or just remove
                //  C "PreChange"        - okey, we got several '-' lines and moved to '+' lines -> this is a change block
                //#########
                if (diffCode == '+') {
                    if (state == 'D') {
                        // ZZ 'change' never begins with '+': just dump out value
                        buf.append("<tr>").append(lnSep);

                        buf.append("<td bgcolor=\"");
                        buf.append(diffColorNil);
                        buf.append("\">").append(lnSep);
                        buf.append(codeFontBeg);
                        buf.append("&nbsp;");
                        buf.append(codeFontEnd);
                        buf.append(lnSep).append("</td>").append(lnSep);

                        buf.append("<td bgcolor=\"");
                        buf.append(diffColorAdd);
                        buf.append("\">").append(lnSep);
                        buf.append(codeFontBeg);
                        buf.append(remStr);
                        buf.append(codeFontEnd);
                        buf.append(lnSep).append("</td>").append(lnSep);

                        buf.append("</tr>").append(lnSep);
                    } else {
                        // ZZ we got minus before
                        state = 'C';
                        rtColV.add(remStr);
                    }
                } else if (diffCode == '-') {
                    state = 'R';
                    ltColV.add(remStr);
                } else {
                    // ZZ empty diffcode
                    HTMLHelper.appendDiffLines(buf, state, ltColV, rtColV);

                    buf.append("<tr>").append(lnSep);

                    buf.append("<td>").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append(remStr);
                    buf.append(codeFontEnd);
                    buf.append(lnSep).append("</td>").append(lnSep);

                    buf.append("<td>").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append(remStr);
                    buf.append(codeFontEnd);
                    buf.append(lnSep).append("</td>").append(lnSep);

                    buf.append("</tr>").append(lnSep);

                    state = 'D';
                    ltColV.clear();
                    rtColV.clear();
                }
            }
        }

        HTMLHelper.appendDiffLines(buf, state, ltColV, rtColV);

        // TODO
        //#state is empty if we didn 't have any change
//        if (!$state) {
//            print "<tr><td colspan=2>&nbsp;</td></tr>";
//            print "<tr bgcolor=\"$diffcolorEmpty\" >";
//            print "<td colspan=2 align=center>";
//            print "<b>- No viewable Change -</b>";
//            print "</td></tr>";
//        }

        buf.append("<tr bgcolor=\"");
        buf.append(lgdBgColor);
        buf.append("\">").append(lnSep);
        buf.append("<td colspan=2>").append(lnSep);

        //
        // LEGEND TABLE
        //
        buf.append("<table width=100% border=1>").append(lnSep);

        buf.append("<tr bgcolor=\"");
        buf.append(lgdTitleColor);
        buf.append("\">").append(lnSep);
        buf.append("<td align=\"center\">").append(lnSep);
        buf.append("<strong>-- Legend --</strong><br>").append(lnSep);

        buf.append("<table width=\"100%\" border=0 cellspacing=0 cellpadding=2>").append(lnSep);

        buf.append("<tr>").append(lnSep);

        buf.append("<td width=\"50%\" align=center bgcolor=\"");
        buf.append(diffColorRem);
        buf.append("\">").append(lnSep);
        buf.append("Removed in v.");
        buf.append(rev1);
        buf.append(lnSep);
        buf.append("</td>").append(lnSep);
        buf.append("<td width=\"50%\" bgcolor=\"");
        buf.append(diffColorNil);
        buf.append("\">&nbsp;");
        buf.append("</td>").append(lnSep);

        buf.append("</tr>").append(lnSep);

        buf.append("<tr bgcolor=\"");
        buf.append(diffColorChg);
        buf.append("\">").append(lnSep);

        buf.append("<td align=\"center\" colspan=2>").append(lnSep);
        buf.append("changed lines").append(lnSep);
        buf.append("</td>").append(lnSep);

        buf.append("</tr>").append(lnSep);

        buf.append("<tr>").append(lnSep);
        buf.append("<td width=\"50%\" bgcolor=\"");
        buf.append(diffColorNil);
        buf.append("\">&nbsp;");
        buf.append("</td>").append(lnSep);

        buf.append("<td width=\"50%\" align=\"center\" bgcolor=\"");
        buf.append(diffColorAdd);
        buf.append("\">").append(lnSep);
        buf.append("Inserted in v.").append(lnSep);
        buf.append(rev2);
        buf.append(lnSep);
        buf.append("</td>").append(lnSep);
        buf.append("</tr>").append(lnSep);

        buf.append("</table>").append(lnSep); // Colors Table

        buf.append("</td>").append(lnSep);
        buf.append("</tr>").append(lnSep);
        buf.append("</table>").append(lnSep); // Legend Table

        buf.append("</td>").append(lnSep);
        buf.append("</tr>").append(lnSep);

        //
        // END DIFF TABLE
        //

        buf.append("</table>").append(lnSep);

        //
        // RAW DIFF
        //
        buf.append("<a name=\"RAW\"></a>").append(lnSep);
        buf.append("<a href=\"#TOP\">Back To Top</a><br>").append(lnSep);
        buf.append("<pre>").append(lnSep);
        buf.append(HTMLHelper.adjustPlainText(rawDiff));
        buf.append(lnSep);
        buf.append("</pre>").append(lnSep);

        return buf;
    }

    private static StringBuilder appendDiffLines(StringBuilder buf, char state, List ltColV, List rtColV) {
        String lnSep = "\r\n";

        String clrRmv = "#FFCCCC";
        String clrChg = "#99FF99";
        String clrChgDk = "#44CC44";
        String clrAdd = "#CCCCFF";
        String clrNil = "#CCCCCC";

        String codeFontBeg = "<font face=\"Helvetica,Arial\" size=\"-1\">";
        String codeFontEnd = "</font>";

        if (state == 'R') {
            // ZZ we just got remove-lines before
            for (Object o : ltColV) {
                buf.append("<tr>").append(lnSep);

                buf.append("<td bgcolor=\"");
                buf.append(clrRmv);
                buf.append("\">").append(lnSep);
                buf.append(codeFontBeg);
                buf.append(o);
                buf.append(codeFontEnd);
                buf.append(lnSep);
                buf.append("</td>").append(lnSep);

                buf.append("<td bgcolor=\"");
                buf.append(clrNil);
                buf.append("\">").append(lnSep);
                buf.append(codeFontBeg);
                buf.append("&nbsp;");
                buf.append(codeFontEnd);
                buf.append(lnSep);
                buf.append("</td>").append(lnSep);

                buf.append("</tr>").append(lnSep);
            }
        } else if (state == 'C') {
            // ZZ state eq "PreChange"
            // ZZ we got removes with subsequent adds
            for (int j = 0; j < ltColV.size() || j < rtColV.size(); ++j) {
                // ZZ dump out both cols
                buf.append("<tr>").append(lnSep);

                if (j < ltColV.size()) {
                    buf.append("<td bgcolor=\"");
                    buf.append(clrChg);
                    buf.append("\">").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append(ltColV.get(j));
                    buf.append(codeFontEnd);
                    buf.append(lnSep);
                    buf.append("</td>").append(lnSep);
                } else {
                    buf.append("<td bgcolor=\"");
                    buf.append(clrChgDk);
                    buf.append("\">").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append("&nbsp;");
                    buf.append(codeFontEnd);
                    buf.append(lnSep);
                    buf.append("</td>").append(lnSep);
                }

                if (j < rtColV.size()) {
                    buf.append("<td bgcolor=\"");
                    buf.append(clrChg);
                    buf.append("\">").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append(rtColV.get(j));
                    buf.append(codeFontEnd);
                    buf.append(lnSep);
                    buf.append("</td>").append(lnSep);
                } else {
                    buf.append("<td bgcolor=\"");
                    buf.append(clrChgDk);
                    buf.append("\">").append(lnSep);
                    buf.append(codeFontBeg);
                    buf.append("&nbsp;");
                    buf.append(codeFontEnd);
                    buf.append(lnSep);
                    buf.append("</td>").append(lnSep);
                }

                buf.append("</tr>").append(lnSep);
            }
        }

        return buf;
    }

    public static String adjustPlainText(String text) {
        int saveIdx = 0;

        int textLen = text.length();

        String result = "<pre>\n" + HTMLHelper.escapeHTML(text) + "\n</pre>\n";

        return result;
    }

    public static String escapeHTML(String text) {
        int saveIdx = 0;
        int textLen = text.length();
        boolean sendNBSP = false;

        StringBuilder result = new StringBuilder(textLen + 2048);

        if (textLen == 0) result.append("&nbsp;");

        for (int i = 0; i < textLen; ++i) {
            char ch = text.charAt(i);

            if (ch == '<') result.append("&lt;");
            else if (ch == '>') result.append("&gt;");
            else if (ch == '&') result.append("&amp;");
            else if (ch == '"') result.append("&quot;");
            else if (ch == ' ') // TODO
            {
                sendNBSP = !sendNBSP;
                if (sendNBSP) result.append("&nbsp;");
                else result.append(" ");
            } else if (ch == '\t') // TODO
            {
                sendNBSP = false;
                result.append("&nbsp;&nbsp;&nbsp; ");
            } else result.append(ch);
        }

        return result.toString();
    }
}
