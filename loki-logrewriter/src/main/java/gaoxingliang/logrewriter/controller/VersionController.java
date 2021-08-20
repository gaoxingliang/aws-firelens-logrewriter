package gaoxingliang.logrewriter.controller;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController("/api/version")
public class VersionController {

    @GetMapping("/api/version")
    public String get() {
        try {
            Resource resource = new ClassPathResource("version.txt");
            return IOUtils.toString(resource.getInputStream());
        }
        catch (Exception e) {
            LOG.warn("Fail to read version", e);
            return "Fail to read version";
        }
    }
}
