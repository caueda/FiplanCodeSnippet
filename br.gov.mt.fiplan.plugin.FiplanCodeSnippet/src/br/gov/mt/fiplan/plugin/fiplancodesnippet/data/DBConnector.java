package br.gov.mt.fiplan.plugin.fiplancodesnippet.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DBConnector {
	
	public static Connection getConnection() throws Exception {
		Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
		return DriverManager.getConnection("jdbc:ucanaccess://C:/Java/CodeSnippetFiplan.mdb");
	}	
	
	public static Map<String, String[]> getMenu() {
		Map<String, List<String>> menu = new HashMap<String, List<String>>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
        try {        	
        	conn=DriverManager.getConnection("jdbc:ucanaccess://C:/Java/CodeSnippetFiplan.mdb");            
            stmt = conn.createStatement();

            // Fetch table
            String selTable = "SELECT * FROM Snippet";
            stmt.execute(selTable);
            rs = stmt.getResultSet();            
            while((rs!=null) && (rs.next())) {
            	String category = rs.getString("category");
            	String alias = rs.getString("alias");
            	if(!menu.containsKey(category)) {
            		menu.put(category, new ArrayList<String>());
            	}
            	menu.get(category).add(alias);            	
            }
        }        
        catch(Exception ex) {
            ex.printStackTrace();
        } finally {        	
        	try {if(stmt != null) stmt.close();} catch(Exception e) {}
        	try {if(conn != null) conn.close();} catch(Exception e) {}
        	try {if(rs != null) rs.close();} catch(Exception e) {}
        }
        Map<String, String[]> menuFinal = new HashMap<String, String[]>();
        for(Entry<String, List<String>> entry : menu.entrySet()) {
        	menuFinal.put(entry.getKey(), entry.getValue().toArray(new String[] {}));
        }
        return menuFinal;
	}
	
	public static String getCode(String alias) throws Exception {
//		Decoder decoder = Base64.getDecoder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select code from Snippet where alias = ?";
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, alias);
			rs = ps.executeQuery();
			if(rs.next()) {
//				return new String(decoder.decode(rs.getString("code")));
				return rs.getString("code");
			}
		} catch(Exception e) {
			
		} finally {
        	try {if(ps != null) ps.close();} catch(Exception e) {}
        	try {if(conn != null) conn.close();} catch(Exception e) {}
        	try {if(rs != null) rs.close();} catch(Exception e) {}
		}
		return "";
	}
	
	public static void insertCode(String category, String alias, String code) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "insert into Snippet(category, alias, code) values (?, ?, ?)";
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, category);
			ps.setString(2, alias);
			ps.setString(3, code);
			if(ps.executeUpdate() > 0) {
				conn.commit();
			}			
		} catch(Exception e) {
			conn.rollback();
		} finally {
        	try {if(ps != null) ps.close();} catch(Exception e) {}
        	try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String[]> menu = getMenu();
		Encoder encoder = Base64.getEncoder();		
		String category = "javascript";
		String alias = "consultarUnidadeGestora";
		String code = "	function consultarUnidadeGestora(obj) {\r\n" + 
				"		var f = document.forms[0];\r\n" + 
				"		if (obj == null) {			\r\n" + 
				"			parent.popUpCenter('pesquisaPopup.do?tipo=unidadeGestoraSIAF&exercicioPesquisa=exercicioExecucaoFinanceiro&flgAtivo=1&cdUO=' + f.cdUnidadeOrcamentariaFiplan.value,750,450);\r\n" + 
				"		} else {\r\n" + 
				"			if(!obj.isModified || f.cdUnidadeGestora.value == '') return;\r\n" + 
				"			f.idUnidadeGestora.value='';			\r\n" + 
				"			refresh();\r\n" + 
				"		}\r\n" + 
				"	}";
		insertCode(category, alias, encoder.encodeToString(code.getBytes()));
		System.out.println("Cadastrado com sucesso!");
	}
}