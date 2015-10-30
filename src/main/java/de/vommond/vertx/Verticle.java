package de.vommond.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Verticle extends AbstractVerticle {
	
		
	private Logger logger = LoggerFactory.getLogger(Verticle.class);

	private HttpServer server;
	

	
	@Override
	public void start() {
		this.logger.info("start() > enter");
	
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create().setMergeFormAttributes(false));
		router.route().handler(CookieHandler.create());
		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)).setNagHttps(false));
	
		StaticHandler handler = StaticHandler.create();
		
		/**
		 * Session
		 */
		router.route(HttpMethod.GET, "/session/:key.json").handler(context->{
			String key = context.request().getParam("key");
			JsonObject value = context.session().get(key);
			if(value!=null){
				context.response().end(value.encode());
			} else {
				context.response().end("{}");
			}
	
		});
		
		
		router.route(HttpMethod.POST, "/session/:key.json").handler(context->{
			JsonObject value = context.getBodyAsJson();
			String key = context.request().getParam("key");
			context.session().put(key, value);
			context.response().end("{}");
		});
		
		router.route(HttpMethod.GET, "/session/delete").handler(context->{
			context.session().destroy();
			context.response().end("{}");
		
			
		});

		router.route().handler(handler).failureHandler(frc -> {
			if(frc.failure()!=null){
				frc.failure().printStackTrace();
			} 
			HttpServerResponse response = frc.response();
			response.sendFile("webroot/index.html");
		});
			
		/**
		 * Launch server
		 */
		HttpServerOptions options = new HttpServerOptions()
			.setCompressionSupported(true);
		
		this.server = vertx.createHttpServer(options)
			.requestHandler(router::accept)
			.listen(8080);
		
		
		logger.info("******************************");
		logger.info("* Vertx launched at 8080     *"); 
		logger.info("******************************");
	}
	

	
	@Override
	public void stop(){
	
		try {
			
		
			server.close();
			
			System.out.println("******************************");
			System.out.println("* Vertx STOP                  *");
			System.out.println("******************************");
	
			super.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
