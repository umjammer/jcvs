/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import com.ice.cvsc.CVSArgumentList;
import com.ice.cvsc.CVSClient;
import com.ice.cvsc.CVSEntryList;
import com.ice.cvsc.CVSProject;
import com.ice.cvsc.CVSRequest;
import com.ice.cvsc.CVSResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-22 nsano initial version <br>
 */
class TestCase {

    @Test
    @DisplayName("gui")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test1() throws Exception {
        com.ice.jcvsii.JCVS.main(new String[] {});

        CountDownLatch cdl = new CountDownLatch(1);
        cdl.await();
    }

    @Test
    @DisplayName("client co, hardcoded")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test2() throws Exception {
        String home = System.getProperty("user.home");
        String rootDir = home + "/Downloads/retrocode";
        Path localDir = Paths.get("tmp/retrocode");
        if (!Files.exists(localDir)) {
            Files.createDirectories(localDir);
        }

        CVSRequest request = new CVSRequest();
        request.parseControlString(":co:N:ANP:deou:");

        CVSClient client = new CVSClient("localhost", 0);
        CVSProject project = new CVSProject(client);

        project.setUserName(System.getProperty("user.name", ""));
        project.setTempDirectory(System.getProperty("java.io.tmpdir"));
        project.setRepository(".");
        project.setRootDirectory(rootDir);
        project.setLocalRootDirectory(localDir.toAbsolutePath().toString());
        project.setPServer(false);
        project.setConnectionPort(0);
        project.setConnectionMethod(CVSRequest.METHOD_LOCAL);

        project.establishRootEntry(rootDir);

        request.setPServer(false);
        request.setUserName(System.getProperty("user.name", ""));
        request.setConnectionMethod(CVSRequest.METHOD_LOCAL);
        request.setPort(0);
        request.setHostName("localhost");
        request.setRepository(".");
        request.setRootDirectory(rootDir);
        request.setRootRepository(rootDir);
        request.setLocalDirectory(localDir.toAbsolutePath().toString());
        request.responseHandler = project;

        request.setEntries(new CVSEntryList());
        request.appendArguments(new CVSArgumentList());

        CVSResponse response = new CVSResponse();
        client.processCVSRequest(request, response);
        project.processCVSResponse(request, response);

        System.err.println("STATUS: " + response.getStatus());
        System.err.println("STDERR: " + response.getStderr());
        System.err.println("STDOUT: " + response.getStdout());
        assertEquals(CVSResponse.OK, response.getStatus());
        assertTrue(Files.exists(localDir.resolve("code")));
        assertTrue(Files.exists(localDir.resolve("code/ButterWorth.cpp")));
        assertTrue(Files.exists(localDir.resolve("CVS/Entries")));
        assertTrue(Files.exists(localDir.resolve("code/CVS/Entries")));
    }
}
