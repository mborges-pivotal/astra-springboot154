package com.datastax.da.astra.investment.frontend.controller;

import javax.servlet.http.HttpServletRequest;

import com.datastax.da.astra.investment.backend.controller.InvestmentApiController;
import com.datastax.da.astra.investment.backend.model.Trade;
import com.datastax.driver.core.utils.UUIDs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * The intent is to have a completely independent Frontend component
 * 
 * - Remove Cassandra driver dependencies. E.g UUIDs utility
 * - We need our own model. E.g. Trade object
 */
@Controller
public class InvestmentController {

    @Autowired
    private InvestmentApiController api;


    @GetMapping("/home")
    public String tradePage(Model model) {
        model.addAttribute("trade", new Trade());
        return "home";
    }

    @PostMapping("/trade")
    public String insertTrade(HttpServletRequest request, @ModelAttribute Trade trade, Model model) {
      model.addAttribute("trade", trade);
      trade.setTradeId(UUIDs.timeBased());
      api.insertTrade(request, trade);
      return "home";
    }

    
}
