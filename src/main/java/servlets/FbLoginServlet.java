package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FbLoginServlet extends BaseServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = prepareResponse(response);
		
		out.println("<span style=\"color:#000000;\"><%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"  \r\n" + 
				"    pageEncoding=\"UTF-8\"%>  \r\n" + 
				"<!DOCTYPE html>  \r\n" + 
				"<html>  \r\n" + 
				"<head>  \r\n" + 
				"<title>Facebook Login Page</title>  \r\n" + 
				"<meta charset=\"UTF-8\">  \r\n" + 
				"</head>  \r\n" + 
				"<script type=\"text/javascript\" src=\"/static/jquery-1.7.2.min.js\"></script>  \r\n" + 
				"<script>  \r\n" + 
				"//引入 facebook SDK  \r\n" + 
				"(function(d, s, id) {  \r\n" + 
				"    var js, fjs = d.getElementsByTagName(s)[0];  \r\n" + 
				"    if (d.getElementById(id))  \r\n" + 
				"        return;  \r\n" + 
				"    js = d.createElement(s);  \r\n" + 
				"    js.id = id;  \r\n" + 
				"    js.src = \"//connect.facebook.net/en_US/sdk.js\";  \r\n" + 
				"    fjs.parentNode.insertBefore(js, fjs);  \r\n" + 
				"}(document, 'script', 'facebook-jssdk'));  \r\n" + 
				"</script>  \r\n" + 
				"<body>  \r\n" + 
				"    <h1>Login Page</h1>  \r\n" + 
				"    <form action=\"${pageContext.request.contextPath }/login\">  \r\n" + 
				"        username: <input type=\"text\" name=\"username\"><br /> <br />  \r\n" + 
				"        password: <input type=\"password\" name=\"password\"><br /> <br />  \r\n" + 
				"        <input type=\"submit\" value=\"登录\">  \r\n" + 
				"    </form><!-- 普通登录 -->  \r\n" + 
				"    <br />  \r\n" + 
				"      \r\n" + 
				"    <fb:login-button scope=\"public_profile,email\"  \r\n" + 
				"        onlogin=\"checkLoginState();\"  \r\n" + 
				"        auto_logout_link=\"true\"  \r\n" + 
				"        size=\"large\"  \r\n" + 
				"        show_faces=\"true\">  \r\n" + 
				"    </fb:login-button><!-- facebook 按钮 -->  \r\n" + 
				"    <div id=\"status\"></div><!-- 登录状态显示 -->  \r\n" + 
				"      \r\n" + 
				"<script>  \r\n" + 
				"  \r\n" + 
				"    function statusChangeCallback(response) {  \r\n" + 
				"        //可用于后台验证，但是前台调用SDK则会自动验证  \r\n" + 
				"        var accessToken=response.authResponse.accessToken;  \r\n" + 
				"        console.log(response.authResponse.accessToken);  \r\n" + 
				"        if (response.status === 'connected') {//sdk会自动保留accessToken，并且验证该请求是否来自我的应用  \r\n" + 
				"            FB.api('/me?fields=name,first_name,last_name,email', function(response) {   \r\n" + 
				"                //将用户信息传回服务端  \r\n" + 
				"                window.location.href=\"http://gntina.iok.la/userInfo?userInfo=\"+JSON.stringify(response);  \r\n" + 
				"                /* $.ajax({ \r\n" + 
				"                        url:\"http://gntina.iok.la/userInfo\", \r\n" + 
				"                        data:{ \r\n" + 
				"                            userInfo:JSON.stringify(response) \r\n" + 
				"                        }, \r\n" + 
				"                        dataType:\"json\", \r\n" + 
				"                        async:false, \r\n" + 
				"                        success:function(data){ \r\n" + 
				"                            window.location.href=\"\"; \r\n" + 
				"                        }  \r\n" + 
				"                    }); */  \r\n" + 
				"                document.getElementById('status').innerHTML =  \r\n" + 
				"                    'Thanks for logging in, ' + response.name + '!';  \r\n" + 
				"            });  \r\n" + 
				"              \r\n" + 
				"        } else {  \r\n" + 
				"            document.getElementById('status').innerHTML = 'Please log '  \r\n" + 
				"                    + 'into this app.';  \r\n" + 
				"        }  \r\n" + 
				"    }  \r\n" + 
				"  \r\n" + 
				"    function checkLoginState() {  \r\n" + 
				"        FB.getLoginStatus(function(response) {  \r\n" + 
				"            statusChangeCallback(response);  \r\n" + 
				"        });   \r\n" + 
				"    }  \r\n" + 
				"  \r\n" + 
				"    window.fbAsyncInit = function() {  \r\n" + 
				"        FB.init({  \r\n" + 
				"            appId : '269265720264443',  \r\n" + 
				"            cookie : true,   \r\n" + 
				"            xfbml : true,   \r\n" + 
				"            version : 'v2.8'   \r\n" + 
				"        });  \r\n" + 
				"  \r\n" + 
				"        FB.getLoginStatus(function(response) {  \r\n" + 
				"            statusChangeCallback(response);  \r\n" + 
				"        });  \r\n" + 
				"  \r\n" + 
				"    };  \r\n" + 
				"  \r\n" + 
				"</script>  \r\n" + 
				"</body>  \r\n" + 
				"</html></span>  ");		
	}
}
