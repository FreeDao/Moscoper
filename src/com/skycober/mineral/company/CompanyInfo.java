package com.skycober.mineral.company;

public class CompanyInfo {

	private String id;// ��עid
	private String eid;// ��ҵid
	private String ename;// ��ҵname
	private long add_time;// ��ӹ�ע��ʱ��
	private String attention_num;// ��ע��
	private boolean is_attention;// �Ƿ񱻹�ע
	private String industry;// ��ҵ
	private String elogo;// ��ҵlogo
	private String praise_num;//���޵�����
	private String black_num;//�����ε�����
	private String sortLetters;  //��ʾ����ƴ��������ĸ
	
	
	
	
	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public String getBlack_num() {
		return black_num;
	}

	public void setBlack_num(String black_num) {
		this.black_num = black_num;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getElogo() {
		return elogo;
	}

	public void setElogo(String elogo) {
		this.elogo = elogo;
	}

	public String getAttention_num() {
		return attention_num;
	}

	public void setAttention_num(String attention_num) {
		this.attention_num = attention_num;
	}

	public boolean isIs_attention() {
		return is_attention;
	}

	public void setIs_attention(boolean is_attention) {
		this.is_attention = is_attention;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}

	@Override
	public String toString() {
		return "CompanyInfo [id=" + id + ", eid=" + eid + ", ename=" + ename
				+ ", add_time=" + add_time + ", attention_num=" + attention_num
				+ ", is_attention=" + is_attention + ", industry=" + industry
				+ ", elogo=" + elogo + ", praise_num=" + praise_num
				+ ", black_num=" + black_num + "]";
	}

	

}
