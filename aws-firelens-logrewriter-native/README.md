# The project
This is a native version of the upper aws firelens log rewriter.
This project is based on quarkus, and it's guide:
Quarkus guide: https://quarkus.io/guides/spring-web

# Changes?
1. the fastjson, log4j is not available in graalvm when building native image. 
2. some classes which can't use initialize are ignored in [application.properties](src/main/resources/application.properties) 

# Build native image files
``
./mvnw verify -Pnative
``
the native file is under `target/aws-firelens-logrewriter-1.0.0-SNAPSHOT-runner`

# Build docker files
``
./mvnw clean package -Dquarkus.container-image.build=true
``
# how to use
``
docker run -e LOKI_HOST=xxx.com -e LOKI_PORT=3100  -p 8080:8080 ed_freshlime/aws-firelens-logrewriter:1.0.0-SNAPSHOT
``
optional retag it:
``
docker tag ed_freshlime/aws-firelens-logrewriter:1.0.0-SNAPSHOT edwardg/aws-firelens-logrewriter:native-1.0.0-SNAPSHOT
docker push edwardg/aws-firelens-logrewriter:native-1.0.0-SNAPSHOT
``


# Comparison between native version and not.
1. The memory is lower 3/4. The previous is about 600MB. now it's about 200MB.
2. The file size decreased from 100MB to 57MB. (it can be improved more...)
3. The image size decrsed from 500M to 60MB.

# References
1. a simple guide of quarkus about how to build native image: https://quarkus.io/guides/building-native-image
2. use quarkus to build docker image: https://quarkus.io/guides/container-image


