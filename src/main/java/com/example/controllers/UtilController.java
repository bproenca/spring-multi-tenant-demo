package com.example.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import com.example.TenantContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UtilController {

    private static final Logger log = LoggerFactory.getLogger(UtilController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(path = "/nls", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getNlsParameters(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList("select parameter, value from NLS_SESSION_PARAMETERS");
        log.info("NLS result {}: ", list);
        return ResponseEntity.ok(list);
    }

    @RequestMapping(path = "/date", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getDate(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        Map<String, Object> map = jdbcTemplate.queryForMap(
                "select sysdate, to_char(sysdate, 'DD/MM/YYYY'), to_char(to_date(sysdate, 'DD/MM/YYYY'), 'DD/MM/YYYY'), to_char(to_date(sysdate, 'DD/MM/YY'), 'DD/MM/YYYY') from dual");
        log.info("Date result: {}", map);
        return ResponseEntity.ok(map);
    }

    @RequestMapping(path = "/proc", method = RequestMethod.GET)
    public ResponseEntity<String> callProc(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList("select parameter, value from NLS_SESSION_PARAMETERS");
        log.info("NLS result {}: ", list);

        jdbcTemplate.update("call MY_PROC (?, ?)", tenantName, LocalDate.now());
        return ResponseEntity.ok("ok\n");
    }

    @RequestMapping(path = "/ins", method = RequestMethod.GET)
    public ResponseEntity<String> insert(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);
        List<Map<String, Object>> list = jdbcTemplate
                .queryForList("select parameter, value from NLS_SESSION_PARAMETERS");
        log.info("NLS result {}: ", list);

        jdbcTemplate.update("INSERT INTO abcd(tenant, valor) VALUES(?,?)", tenantName, LocalDate.now());
        return ResponseEntity.ok("ok\n");
    }

    @RequestMapping(path = "/proc2", method = RequestMethod.GET)
    public ResponseEntity<String> callProc2(@RequestHeader("X-TenantID") String tenantName) {
        TenantContext.setCurrentTenant(tenantName);

        List list = entityManager.createNativeQuery("select parameter || '=' || value from NLS_SESSION_PARAMETERS").getResultList();
        log.info("NLS result {}: ", list);

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("MY_PROC");
        storedProcedureQuery.registerStoredProcedureParameter("TENANT", String.class, ParameterMode.IN);
        storedProcedureQuery.setParameter("TENANT", tenantName);
        storedProcedureQuery.registerStoredProcedureParameter("VALOR", LocalDate.class, ParameterMode.IN);
        storedProcedureQuery.setParameter("VALOR", LocalDate.now());
        storedProcedureQuery.execute();
        
        return ResponseEntity.ok("ok\n");
    }
}
