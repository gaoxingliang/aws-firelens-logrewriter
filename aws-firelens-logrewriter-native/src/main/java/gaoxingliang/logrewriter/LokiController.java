/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaoxingliang.logrewriter;

import org.graalvm.nativeimage.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
public class LokiController {

    @PostMapping("/loki/api/v1/push")
    public void hello(@RequestBody String body) {
        JsonSender.log(body);
    }


    @GetMapping("/dump")
    public void dumpHeap() {
        createHeapDump(false);
    }

    /*
     * Generate heap dump and save it into temp file
     */
    private static void createHeapDump(boolean live) {
        try {
            File file = File.createTempFile("app", ".hprof");
            VMRuntime.dumpHeap(file.getAbsolutePath(), live);
            System.out.println("  Heap dump created " + file.getAbsolutePath() + ", size: " + file.length());
        }
        catch (UnsupportedOperationException unsupported) {
            System.out.println("  Heap dump creation failed." + unsupported.getMessage());
        }
        catch (IOException ioe) {
            System.out.println("IO went wrong: " + ioe.getMessage());
        }
    }
}
