FROM lonelyplanet/scala

RUN mkdir /op-sitemap-aggregator-poc
WORKDIR /op-sitemap-aggregator-poc
ADD . /op-sitemap-aggregator-poc

RUN sbt clean compile

CMD sbt run