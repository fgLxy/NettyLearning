package org.my.netty.http.file.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpFileServerHandler extends
		SimpleChannelInboundHandler<FullHttpRequest> {
	private final String url;
	private final String root;

	public HttpFileServerHandler(String url) {
		this.url = url;
		this.root = System.getProperty("user.dir");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx,
			FullHttpRequest request) throws Exception {
		if(!request.decoderResult().isSuccess()) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		String uri = request.uri();
		Path path = getPath(uri);
		if(path == null) {
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
			return;
		}
		if(!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			dump(ctx, "不存在的文件或文件夹");
			return;
		}
		if(Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			String html = directoryHTML(path, uri);
			printDirectory(ctx, uri, html);
		} else {
			
			printFile(ctx, path, request);
		}
	}

	private String directoryHTML(Path path, String uri) throws IOException {
		String prevPath = getPrevPath(path);
		StringBuilder builder = new StringBuilder();
		builder.append("<li><p><a style='color:green;' href='" + prevPath + "'>/..</a></p></li>");
		String liList =  Files.list(path).count() > 0 ? (Files.list(path).sorted((p1, p2) -> comparePath(p1, p2))
				.map(p -> 
			(Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS) ? 
					"<li><p><a style='color:green;' " 
					: "<li><p><a style='color:red;' target='_blank' ") + "href='" + uri + p.getFileName() + "/'>" + p.getFileName() + "</a></p></li>" 
		).reduce((html, append) -> html + append).get()) : "";
		builder.append(liList);
		return "<ul>" + builder.toString() + "</ul>";
	}

	private int comparePath(Path p1, Path p2) {
		boolean directory1 = Files.isDirectory(p1, LinkOption.NOFOLLOW_LINKS);
		boolean directory2 = Files.isDirectory(p2, LinkOption.NOFOLLOW_LINKS);
		return directory1 == directory2 ? p2.compareTo(p1) : directory1 ? -1 : 1;
	}

	private String getPrevPath(Path path) {
		String prevStr = path.getParent().toString();
		String prevPath = prevStr.length() > root.length() ? prevStr.substring(root.length()) : "/";
		return prevPath.endsWith("/") ? prevPath : prevPath + "/";
	}

	private Path getPath(String uri) {
		if(!uri.startsWith(url)) {
			return null;
		}
		return Paths.get(root + uri);
	}
	
	public void printFile(ChannelHandlerContext ctx, Path path, FullHttpRequest request) throws IOException {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		StringBuilder builder = new StringBuilder();
		builder.append("<!doctype html><head><title>debug</title></head><body><h1>")
				.append(path.toFile().getName()).append("</h1><pre style='word-wrap: break-word; white-space: pre-wrap;'>");
		
		List<String> lines = Files.readAllLines(path);
		for(String line : lines) {
//			line = line.replaceAll("&", "&amp;")
//					.replaceAll(" ", "&nbsp;")
//					.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
//					.replaceAll("<", "&lt;")
//					.replaceAll(">", "&gt;")
//					.replaceAll("\"", "&quot;");
					
			builder.append(line).append("\n");//.append("<h2>").append(line).append("</h2>");
		}
		builder.append("</pre></body>");
		
		ByteBuf responseBuf = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
		response.content().writeBytes(responseBuf);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	public void printDirectory(ChannelHandlerContext ctx, String name,
			String html) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		StringBuilder builder = new StringBuilder();
		builder.append("<!doctype html><head><title>debug</title></head><body><h1>").append(name).append("</h1>")
				.append(html).append("</body>");
		ByteBuf responseBuf = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
		response.content().writeBytes(responseBuf);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}
	
	public void dump(ChannelHandlerContext ctx,
			String message) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		StringBuilder builder = new StringBuilder();
		builder.append("<!doctype html><head><title>debug</title></head><body><h1>").append(message).append("</h1></body>");
		ByteBuf responseBuf = Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
		response.content().writeBytes(responseBuf);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	public void sendError(ChannelHandlerContext ctx,
			HttpResponseStatus status) {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

}
