
package com.flakks.spelling.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.flakks.spelling.QueryMapper;
import com.flakks.spelling.SpecialRule;
import com.flakks.spelling.SpellingLookup;

public class CorrectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long time = System.currentTimeMillis();
		String locale = request.getParameter("locale");
		String query = request.getParameter("query").toLowerCase();
		
		query = SpecialRule.deduplicates(query);
		SpellingLookup spellingLookup = new SpellingLookup(locale);
		QueryMapper queryMapper = new QueryMapper(spellingLookup);
			
		String mappedQuery = queryMapper.map(query);
			
		time = System.currentTimeMillis() - time;
		
		JSONObject json = new JSONObject();
		json.put("query", mappedQuery);
		json.put("took", time);
		json.put("distance", spellingLookup.getSumDistance());

    	response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(json.toString());
    }
}
