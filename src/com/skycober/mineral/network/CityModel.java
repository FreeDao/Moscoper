package com.skycober.mineral.network;

import java.util.List;

public class CityModel {
	private List<CityItemModel> Result;

	public List<CityItemModel> getResult() {
		return Result;
	}

	public void setResult(List<CityItemModel> result) {
		Result = result;
	}

	public class CityItemModel {
		private String id;
		private String pid;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
