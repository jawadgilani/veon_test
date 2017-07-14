package com.silkbank.consumersBanking.commons;


import org.apache.spark.sql.api.java.UDF3;

/**
 * <p>
 * Collection of utility UDFs
 */
public class Functions {

    /**
     * Derive NAME for Destination table
     */
    public static UDF3<String, String, String, String> name_code = new UDF3<String, String, String, String>() {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
         * Derive location from DPI table data
         *
         * @param FNAME
         * @param MNAME
         * @param LNAME
         * @return
         * @throws Exception
         */
        public String call(String FNAME, String MNAME, String LNAME) throws Exception {
       	
        	FNAME = FNAME.substring(0, 1)  ;
        	MNAME = MNAME.substring(0, 1)  ;
        	LNAME = LNAME.substring(0, 1)  ;
        	
        	
        	String cellid = FNAME + "" + MNAME + "" + LNAME ;
     
                return cellid;
          
        }
    };
}

/*
ORIGINAL LOCATION QUERY


"select concat(coalesce(lacid,''),case when ind =1 then substring(cellid,2,4) when ind=2 then substring(cellid,1,4) when ind=3 then cellid end) as location
from
(
select
conv(lacid,16,10) as lacid,
ind,
conv(cellid,16,10) as cellid
from
(
select
case when cgi is not null and cgi <>'' then  substring(cgi,7,4) when sai is not null and sai <>'' then  substring(sai,7,4) when ecgi is not null and ecgi <>'' then  null end as lacid,
case when cgi is not null and cgi <>'' then  substring(cgi,11,4) when sai is not null and sai <>'' then  substring(sai,11,4) when ecgi is not null and ecgi <>'' then  substring(ecgi,7,5)  end as cellid,
case when cgi is not null and cgi <>'' then  1 when sai is not null and sai <>'' then  2 when ecgi is not null and ecgi <>'' then  3  end as ind
 from dd_dpi_stg.dpi_udn
 limit 10
) as a
) as b
;"
 */
