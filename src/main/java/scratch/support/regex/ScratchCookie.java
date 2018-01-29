package scratch.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ScratchCookie {

	public Map<String, String> loadCookie() {

		Map<String, String> cookieMap = new HashMap<String, String>();
		cookieMap.put("Cookie",
				"ipb_member_id=1441493; ipb_pass_hash=ade8476023c527082d6ada40edd4cff2; igneous=3b1e5886f9cb299ef259c8313bb296e40cbe0b36e6d696c12c07a791e9c1db3a1e5da8de1ba115b83bf926e90e6adc875cae072274359393bedfd408abdf9a5c; s=883ce52d64f17b2fb8e2bb63957ceaf4aec3fd3c22d665f5cc947e1b628f92b204e2d39e13512f264305d61788cf8fa6eaed2686b2bbcd7d52408f81a6865334; lv=1472033875-1472041809; uconfig=uh_y-lt_m-rc_0-tl_r-cats_0-xns_0-ts_m-tr_2-prn_y-dm_l-ar_0-rx_0-ry_0-ms_n-mt_n-cs_a-fs_p-to_a-pn_0-sc_0-ru_rrggb-xr_a-sa_y-oi_n-qb_n-tf_n-hh_-hp_-hk_-xl_");
		cookieMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
		cookieMap.put("Cache-Control", "max-age=0");
		return cookieMap;
	}
}
