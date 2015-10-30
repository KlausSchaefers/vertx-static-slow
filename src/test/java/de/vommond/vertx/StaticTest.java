package de.vommond.vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RunWith(VertxUnitRunner.class)
public class StaticTest extends BaseTestCase {
	
	private Logger logger = LoggerFactory.getLogger(StaticTest.class);
	
	@Test
	public void test(TestContext context){
		logger.info("test() > entry");
		
		deploy(new Verticle(), context);
		
	
		for(int i=0; i < 100; i++){
			getString("");
			getString("index.html");
		}
		
	
		logger.info("test() > waiting to end...");
		sleep(5000);
		
		logger.info("test() > exit");
	}

}
