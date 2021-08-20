FROM  edwardg/freshlime-pipelinebase:1.1
MAINTAINER edward
LABEL name="logrewriter"

VOLUME /data/logs

COPY logrewriter-0.0.1.jar /data/
COPY start.sh /data
RUN chmod +x /data/start.sh
WORKDIR /data
EXPOSE 24223
ENTRYPOINT /data/start.sh