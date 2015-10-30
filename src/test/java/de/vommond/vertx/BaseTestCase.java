package de.vommond.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;

import com.google.common.io.CharStreams;


public class BaseTestCase {

	protected Vertx vertx;

	private BasicCookieStore cookieStore;
	
    protected CloseableHttpClient httpClient;
    
    private int loglevel = 3;

	
	@Before
	public void before(TestContext contex) {
		

		vertx = Vertx.vertx();
			
		cookieStore = new BasicCookieStore();
		
	    httpClient = HttpClients.custom()
	             .setDefaultCookieStore(cookieStore)
	             .build();

		
	}

	@After
	public void after(TestContext contex) {
		try {
			
			//vertx.undeploy(vertileID);
			
			vertx.close();
		
			httpClient.close();
		
		} catch (Throwable e) {
			
		}
	}
	

	protected void sleep() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
	}
	
	protected void sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {}
	}


	
	public void cleanUp(){		  
		  
				  
	}
	
	
	
	public void debug(String method, String message) {
		log(4, method, message);
	}
	
	public void log(String method, String message) {
		log(3, method, message);
	}

	public void log(int level, String method, String message) {
		if(level <= loglevel)
			System.out.println(this.getClass().getSimpleName() + "." + method + "() > " + message);
	}

	public void print(List<JsonObject> results) {
		log("print", "#" + results.size() + " ");
		for (JsonObject result : results)
			log("print", result.encodePrettily());

	}
	
	public void print(JsonArray results) {
		log("print", "#" + results.size() + " ");
		
		log("print", results.encodePrettily());

	}
	
	public void deploy(Verticle v, TestContext context){
		
		CountDownLatch l = new CountDownLatch(1);
		

		
	
		DeploymentOptions options = new DeploymentOptions();
		
		vertx.deployVerticle(v, options, new Handler<AsyncResult<String>>() {
			
			@Override
			public void handle(AsyncResult<String> event) {
				
				if(event.succeeded()){
					log("deploy","exit > "  + event.result());
				
				} else {
					//context.fail("Could not deploy verticle");
					event.cause().printStackTrace();
				}
				
			
				l.countDown();
			}
		});
		
		try {
			l.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	
	public String getString(String url){
		
		url = "http://localhost:8080/" + url;
		try {
			long start = System.currentTimeMillis();
			HttpGet httpget = new HttpGet(url);
	
	        CloseableHttpResponse resp = httpClient.execute(httpget);
	        
	        if(resp.getStatusLine().getStatusCode() == 200){
				

				InputStream is = resp.getEntity().getContent();
 
				String result = CharStreams.toString( new InputStreamReader(is ));
		
				long end = System.currentTimeMillis();
				
				debug("getString", "exit > " + url+ " took :" + (end - start) + "ms");
			
				resp.close();
				return result;
				
			} else {
				resp.close();
			}

	    
	      
		} catch (Exception e) {
			e.printStackTrace();
			
		}
      
	
		return null;
		
	}
	
	public JsonObject post(String url, JsonObject data){
	
		url = "http://localhost:8080" + url;
		try{
		
			HttpPost post = new HttpPost(url);
			 
			StringEntity input = new StringEntity(data.encode());
			input.setContentType("application/json");
			post.setEntity(input);
		 
			long start = System.currentTimeMillis();
			CloseableHttpResponse resp = httpClient.execute(post);
			if(resp.getStatusLine().getStatusCode() == 200){
				InputStream is = resp.getEntity().getContent();
 				String json = CharStreams.toString( new InputStreamReader(is ));
				resp.close();
				long end= System.currentTimeMillis();
				debug("post", "exit > " + url+ " took :" + (end - start) + "ms");
				return new JsonObject(json);
			} else {
				resp.close();
				return new JsonObject().put("error",resp.getStatusLine().getStatusCode() );
			}
		
			
		} catch(Exception e){
			e.printStackTrace();
		
			return new JsonObject().put("error", "error");
		}
		
	
	}
	
	public JsonObject get(String url){

		url = "http://localhost:8080" + url;
		try {
			
			HttpGet httpget = new HttpGet(url);
			long start = System.currentTimeMillis();
	        CloseableHttpResponse resp = httpClient.execute(httpget);
	        
	        if(resp.getStatusLine().getStatusCode() == 200){
				
				InputStream is = resp.getEntity().getContent();
 
				String json = CharStreams.toString( new InputStreamReader(is ));
				resp.close();
				long end= System.currentTimeMillis();
				
				debug("get", "exit > " + url+ " took :" + (end - start) + "ms");
				
				return new JsonObject(json);
				
			} else {
				  resp.close();
				return new JsonObject().put("error",resp.getStatusLine().getStatusCode() );
			}

	    
	      
		} catch (Exception e) {
			e.printStackTrace();
			return new JsonObject().put("error", "error");
		}
      
	
		
	}
	

	
	
}
