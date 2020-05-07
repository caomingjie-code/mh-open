package com.mh.base.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页对象  * @author CMJ(曹明杰)  * @date 2017-3-19 上午9:22:11  
 */
public class FPage {

	private int pageNum=1;// 前台传来的页码  别名currentPage，最小为1 ，最大为allPageSize

	private int pageSize = 2;// 每页显示的(数据)条数

	private int begeinIndex=0;// 查询分页开始索引(limit a , b 指这里的的a)

	private int allCount = 0;// 数据总条数 

	private int allPageNum;// 总页数     

	private int firstPage;// 首页

	private int endPage;// 尾页
	/** 利用窗户函数的思想：把前台传来的当前页当作窗户的中心（窗户的中心索引位置：长度（基数最好--）加+1再除于2，偶数则加+2后再除于2） **/
	private int viewFirstPageNnm;// 逻辑上的起始页---窗户最左端   （为任意正负数）

	private int viewEndPageNum;// 逻辑上结束页---窗户最右端  （为任意正负数）

	private int[] first_endPageNum;// 存放显示的页码---窗户的范围内容

	private int viewPageSize=10; // jsp页面要显示的页码数数量（针对first_endPageNum 中的个数） 默认为10

	private List<Object> list = new ArrayList<Object>();// 存放查询对象的集合

	private boolean hasNextPage = false;// 判断是否有下一页(默认没有)
	
	private Map<String,Object> params = new HashMap<String,Object>();//请求参数

	/**
	 * 分页开始初始化数据
	 * 
	 * @param allCount 从数据库查询的总条数
	 * @param list 从数据库中查询的数据
	 */
	public void initFPage(int allCount,List<Object> list) {
		//初始化分页的前提条件 allCount > 0, list.size>0
		if(allCount<=0||list==null||list.size()<=0) {
			return;
		}
		this.list = list;
		//this.pageNum = pageNum;
		this.allCount = allCount;
		/** 计算总页数 **/
		this.allPageNum = this.allCount % pageSize == 0 ? this.allCount / pageSize : this.allCount / pageSize + 1;

		if (pageNum <= 0)
			this.pageNum = 1;
		if (pageNum > this.allPageNum)
			this.pageNum = this.allPageNum;

		/** 计算查询的开始索引值 **/
		this.begeinIndex = (this.pageNum - 1) * this.pageSize;
		/** 开始页码默认值为1 **/
		this.firstPage = 1;
		/** 计算尾页码(总页数就是为尾页码) **/
		this.endPage = this.allPageNum;
        //算出窗口 最前端和最后端的位置
		if (this.allPageNum > this.viewPageSize) {// 总页数一定大于页数长度
			if (this.viewPageSize / 2 == 0) {// 如果是偶数
				this.viewFirstPageNnm = this.pageNum - ((this.viewPageSize + 2) / 2 - 1);
				this.viewEndPageNum = this.pageNum + (this.viewPageSize - ((this.viewPageSize + 2) / 2));
			} else {// 奇数
				this.viewFirstPageNnm = this.pageNum - ((this.viewPageSize + 1) / 2 - 1);
				this.viewEndPageNum = this.pageNum + (this.viewPageSize - ((this.viewPageSize + 1) / 2));
			}
		}
		/** 两种情况:1：显示页数大于等于总页数 viewPageSize>allPageSize, 2：显示页数小于总页数 **/
		if (this.viewPageSize >= this.allPageNum) {
			first_endPageNum = new int[this.allPageNum];// 指定长度
            //存放页码
			for (int i = 0; i < this.allPageNum; i++) {
				first_endPageNum[i] = i + 1;
			}
		} else {
            //-------------------
			first_endPageNum = new int[this.viewPageSize];
            //判断当前 左窗口端是否小于1  默认 1 2 3 4  ...viewPageSize 包含1
			if (this.viewFirstPageNnm < 1) {
				for (int i = 0; i < this.viewPageSize; i++) {
					first_endPageNum[i] = i + 1;
				}
			} // 判断当前 右窗口端是否超出了页数的最大值 例如 7 8 9 ...viewPageSize 不包括7
			else if (this.viewEndPageNum > this.allPageNum) {
				for (int i = 0; i < this.viewPageSize; i++) {
					first_endPageNum[i] = this.allPageNum - (this.viewPageSize - (i + 1));
				}
			} else {// 窗口的前后端都在范围之内
				first_endPageNum = new int[this.viewPageSize];
				for (int i = 0; i < this.viewPageSize; i++) {
					first_endPageNum[i] = this.viewFirstPageNnm + i;
				}
			}
            //------------------
		}
	}

	public boolean isHasNextPage() {
		if (this.allPageNum > this.pageNum)
			this.hasNextPage = true;
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public List<Object> getList() {
		return list;
	}

	public int[] getFirst_endPageNum() {
		return first_endPageNum;
	}


	public void setList(List<Object> list) {
		this.list = list;
	}

	public int getPageNum() {
		return pageNum;
	}
    
	public void setPageNum(int pageNum) {
		if(pageNum<=0) {
			pageNum = 1;
		}
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}


	public int getBegeinIndex() {
		if(this.pageNum<=0) {this.pageNum = 1;}
		this.begeinIndex = (this.pageNum - 1) * this.pageSize;
		return begeinIndex;
	}

	public void setBegeinIndex(int begeinIndex) {
		this.begeinIndex = begeinIndex;
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public int getAllPageNum() {
		return allPageNum;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	

	/**
	 * 初始化方法
	 * @param pageNum 前台传来的页码
	 * @param params  前台传入的查询参数
	 */
	public FPage(Integer pageNum, Map<String, Object> params) {
		if(pageNum==null) {pageNum = 1;}
		this.pageNum = pageNum;
		this.begeinIndex = (this.pageNum - 1) * this.pageSize;
		this.params = params;
	}

	
}