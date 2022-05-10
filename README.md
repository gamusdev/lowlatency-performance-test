# lowlatency-performance-test
Comparison between Aeron &amp; Vega vs. other solutions

# Launch Vega Autodiscovery
java -cp ./target/aeron-vega-test-1.0.0-SNAPSHOT-jar-with-dependencies.jar com.bbva.kyof.vega.autodiscovery.daemon.UnicastDaemonLauncher -ed -sn 192.168.1.0/24

# Launch aeron-vega-test
### As subscriber
java -cp ./target/aeron-vega-test-1.0.0-SNAPSHOT-jar-with-dependencies.jar com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher -f /tmp/vega/subscriber.xml
### As publisher
java -cp ./target/aeron-vega-test-1.0.0-SNAPSHOT-jar-with-dependencies.jar com.gamusdev.lowlatency.performance.tests.aeronvega.Launcher -t pub -f /tmp/vega/publisher.xml
