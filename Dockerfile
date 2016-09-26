FROM neo4j
ENV MAJRIX_HOME /var/lib/majrix
ENV LEIN_HOME $HOME/bin
ENV LEIN_ROOT true
RUN curl -o $LEIN_HOME/lein https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
RUN chmod +x $LEIN_HOME/lein
RUN apt-get update -y && apt-get install git -y
RUN cd && git clone https://github.com/jeff-flower/Majrix.git $MAJRIX_HOME && cd $MAJRIX_HOME
RUN cd $MAJRIX_HOME && git checkout develop && lein deps
CMD ["neo4j"]
