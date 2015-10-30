package de.vommond.vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RunWith(VertxUnitRunner.class)
public class PerformanceTest extends BaseTestCase {
	
	private Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
	
	//@Test
	public void testStatic(TestContext context){
		logger.info("testStatic() > entry");
		
		deploy(new Verticle(), context);
		
	
		for(int i=0; i < 100; i++){
			getString("");
			getString("index.html");
		}
		
	
		logger.info("testStatic() > waiting to end...");
		sleep(1000);
		
		logger.info("testStatic() > exit");
	}

	@Test
	public void testSession(TestContext context){
		logger.info("testSession() > entry");
		
		deploy(new Verticle(), context);
	
		for(int r =0; r < 5; r++){
			logger.info("testSession() > Run " + r);
			for(int i=0; i < 2; i++){
				post("/session/1.json", new JsonObject().put("i", i));
				JsonObject sessionValue = get("/session/1.json");
				context.assertEquals(i, sessionValue.getInteger("i"));
			}
			get("/session/delete");
			
			/**
			 * Here we read session
			 */
			long start= System.currentTimeMillis();
			JsonObject sessionValue = get("/session/1.json");
			long end= System.currentTimeMillis();
			context.assertEquals(false, sessionValue.containsKey("i"));
			logger.info("testSession() > Get(session data) after session.destroy() : " + (end-start) +"ms");
		}
	
		
		for(int r =0; r < 5; r++){
			logger.info("testSession() > Run " + r);
			for(int i=0; i < 2; i++){
				post("/session/1.json", new JsonObject().put("i", i));
				JsonObject sessionValue = get("/session/1.json");
				context.assertEquals(i, sessionValue.getInteger("i"));
			}
			get("/session/delete");
			
			/**
			 * Here we load a statc
			 */
			long start= System.currentTimeMillis();
			getString("index.html");
			long end= System.currentTimeMillis();
		
			logger.info("testSession() > Get(static) session.destroy() : " + (end-start) +"ms");
		}
	
		logger.info("testSession() > exit");
	}
}
