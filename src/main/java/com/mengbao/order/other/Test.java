package com.mengbao.order.other;

import java.util.List;

import com.mengbao.basicClient.beans.CompanyAttrDTO;
import com.mengbao.basicClient.client.BasicService;
import com.mengbao.basicClient.utils.BasicClientException;

public class Test {
	
	private static BasicService basicService = BasicService.getInstance(7101, "121.43.150.38");
	
	public static void main(String[] args) {
		try {
			List<CompanyAttrDTO> list = basicService.getAllCompany();
            System.out.println(list);
		} catch (BasicClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("dev1");
	}
}