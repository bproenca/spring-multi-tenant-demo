package com.example.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.TenantContext;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.hibernate.HikariConfigurationUtil;
import com.zaxxer.hikari.pool.HikariPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UtilController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(path = "/nls", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getNlsParameters(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList("select parameter, value from NLS_SESSION_PARAMETERS");
        return ResponseEntity.ok(list);
    }

    @RequestMapping(path = "/date", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDate(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        Map<String, Object> map = jdbcTemplate.queryForMap(
                "select sysdate, to_char(sysdate, 'DD/MM/YYYY'), to_char(to_date(sysdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), to_char(to_date(sysdate, 'DD/MM/YY'), 'DD/MM/YYYY') from dual");
        return ResponseEntity.ok(map);
    }
}
