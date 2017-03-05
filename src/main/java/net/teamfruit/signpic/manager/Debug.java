package net.teamfruit.signpic.manager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

public class Debug {

	public static void main(final String[] args) throws Exception {
		//		final HttpUriRequest get = new HttpGet("http://bit.ly/2lIT74U");
		final HttpUriRequest get = new HttpGet("https://goo.gl/LNkt4x");
		final HttpResponse response = Downloader.downloader.client.execute(get, HttpClientContext.create());
		System.out.println(response.getFirstHeader("Content-Type").getValue());
		get.abort();
	}
}
