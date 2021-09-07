# about
This docker image has a graalvm enterprise version.

# how to
This docker file requires a graalvm.tar.gz file.
I generated it manually by:
1. download a graalvm-ee version: graalvm-ee-java11-linux-amd64-21.2.0.1.tar.gz
2. install the native-image by: `gu install native-image`
3. rezip it into graalvm.tar.gz

# reference
1. the graalvm.tar.gz: https://download.csdn.net/download/scugxl/22007989
