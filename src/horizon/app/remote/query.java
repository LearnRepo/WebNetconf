package horizon.app.remote;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import horizon.app.broker.SSHNetconf3;

/**
 * Servlet implementation class query
 */
@WebServlet("/query")
public class query extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public query() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		String host = request.getParameter("host");
		System.out.print(request.getParameter("port"));
		int port = Integer.parseInt(request.getParameter("port"));
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String query = request.getParameter("query");
		
		System.out.println(query);
		
		SSHNetconf3 cli = new SSHNetconf3();
		String rpc_r = cli.executeQuery(host, port, user, password, query);
		if(rpc_r != null)
		response.getWriter().write(rpc_r);
		else
			response.getWriter().write("none");
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	public String replaceWithPattern(String str,String replace){
        
        Pattern ptn = Pattern.compile("\\s+");
        Matcher mtch = ptn.matcher(str);
        return mtch.replaceAll(replace);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String host = request.getParameter("host");
		int port = Integer.parseInt(request.getParameter("port"));
		String user = request.getParameter("user");
		String password = request.getParameter("password");
		String query = request.getParameter("query");
		//query = this.replaceWithPattern(query, " ");
		//query = query.replace("\n", "").replace(" ", "&amp;");
		System.out.println(query);
		
		SSHNetconf3 cli = new SSHNetconf3();
		String rpc_r = cli.executeQuery(host, port, user, password, query);
		
		if(rpc_r != null)
			response.getWriter().write(rpc_r);
			else
				response.getWriter().write("none");
	}

}
