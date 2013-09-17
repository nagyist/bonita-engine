/**
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.reporting;

import static org.junit.Assert.assertEquals;

import org.bonitasoft.engine.CommonAPITest;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchOptionsImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Matthieu Chaffotte
 */
public class ReportingAPIIT extends CommonAPITest {

    private static String lineSeparator = "\n";

    @Before
    public void setUp() throws BonitaException {
        login();
        getIdentityAPI().createUser("matti", "bpm", "Matti", "Mäkelä");
    }

    @After
    public void tearDown() throws BonitaException {
        getIdentityAPI().deleteUser("matti");
        logout();
    }

    @Test
    public void reportNumberOfUsers() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT COUNT(*) as nb FROM user_");
        Assert.assertTrue(("nb" + lineSeparator + "1" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void reportUsers() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT userName, lastname FROM user_");
        assertEquals("USERNAME,LASTNAME" + lineSeparator + "matti,Mäkelä" + lineSeparator, csvUsers);
    }

    @Test
    public void reportUsersusingAlias() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT userName AS name, lastname FROM user_");
        assertEquals("NAME,LASTNAME" + lineSeparator + "matti,Mäkelä" + lineSeparator, csvUsers);
    }

    @Test
    public void test() throws BonitaException {
        final SearchOptions options = new SearchOptionsImpl(0, 10);
        final SearchResult<Report> reports = getReportingAPI().searchReports(options);
        Assert.assertEquals(0, reports.getCount());
    }

    @Test
    public void checkSQLValidityOfProcessInstanceAverageTime() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("(CS.STARTDATE/86400000) as CS_START, ");
        builder.append("CS.ENDDATE AS CS_END_DATE, ");
        builder.append("(CS.ENDDATE/86400000) as CS_END, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME, ");
        builder.append("( CS.ENDDATE - CS.STARTDATE ) AS CS_DURATION ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.ENDDATE BETWEEN 1369173600565 AND 1369864799565 ");
        builder.append("ORDER BY 14 DESC, 6, 4");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("CS_PROCESS_DEFINITION_ID,CS_NAME,CS_STATE_ID,CS_START_DATE,CS_START,CS_END_DATE,CS_END,CS_ID,CS_SOURCEOBJECTID,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME,CS_DURATION" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfProcessInstancesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("null AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (6) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600470 AND 1369864799470 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (6) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600470 AND 1369864799470 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("CS_PROCESS_DEFINITION_ID,CS_NAME,CS_STATE_ID,CS_START_DATE,CS_ID,CS_SOURCEOBJECTID,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfNumberOfProcessInstancesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS_TABLE.CS_STATE_ID, ");
        builder.append("CS_TABLE.CS_START_DATE, ");
        builder.append("count(*) as CS_COUNT ");
        builder.append("FROM ");
        builder.append("( ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("(CS.STARTDATE/86400000) AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("null AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (1) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600170 AND 1369864799170 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("(CS.STARTDATE/86400000) AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (1) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600170 AND 1369864799170 ");
        builder.append(") CS_TABLE ");
        builder.append("GROUP BY CS_TABLE.CS_STATE_ID, CS_TABLE.CS_START_DATE ");
        builder.append("ORDER BY 2, 1 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("CS_STATE_ID,CS_START_DATE,CS_COUNT" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfActivitiesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM flownode_instance TSK ");
        builder.append("INNER JOIN process_instance CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN process_instance  CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.SOURCEOBJECTID AS CS_ID, ");
        builder.append("'ARCHIVED' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN arch_process_instance CS ON TSK.PARENTCONTAINERID = CS.SOURCEOBJECTID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("TSK_FLOW_NODE_DEFINITION_ID,TSK_DISPLAY_NAME,TSK_STATE_NAME,TSK_EXPECTED_END_DATE,CS_ID,CS_STATE_NAME,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfListOfProcesses() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME ");
        builder.append("FROM process_definition APS ");
        builder.append("WHERE APS.PROCESSID = -1 ");
        builder.append("AND APS.TENANTID = 1");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("APS_PROCESS_ID,APS_NAME" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfNumberOfActivitiesInStateOpenArchvedAndFailed() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("TSK_TABLE.TSK_STATE_NAME, ");
        builder.append("TSK_TABLE.TSK_EXPECTED_END_DATE, ");
        builder.append("count(*) as TSK_COUNT ");
        builder.append("FROM ");
        builder.append("( ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM flownode_instance TSK ");
        builder.append("INNER JOIN process_instance CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN process_instance  CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.SOURCEOBJECTID AS CS_ID, ");
        builder.append("'ARCHIVED' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN arch_process_instance CS ON TSK.PARENTCONTAINERID = CS.SOURCEOBJECTID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append(") TSK_TABLE ");
        builder.append("GROUP BY TSK_TABLE.TSK_STATE_NAME, TSK_TABLE.TSK_EXPECTED_END_DATE ");
        builder.append("ORDER BY 2, 1 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        Assert.assertTrue(("TSK_STATE_NAME,TSK_EXPECTED_END_DATE,TSK_COUNT" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

}