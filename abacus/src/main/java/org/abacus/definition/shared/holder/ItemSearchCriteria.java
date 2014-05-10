package org.abacus.definition.shared.holder;

import java.io.Serializable;

import org.abacus.definition.shared.constant.EnumList.DefItemClassEnum;
import org.abacus.definition.shared.constant.EnumList.DefItemTypeEnum;

@SuppressWarnings("serial")
public class ItemSearchCriteria implements Serializable {

	private String organization;
	private DefItemTypeEnum itemType;
	private DefItemClassEnum itemClass;
	private String codeLike;
	private String nameLike;

	private Integer first;
	private Integer pageSize;

	public ItemSearchCriteria(String organization, DefItemTypeEnum itemType,
			DefItemClassEnum itemClass) {
		this.itemType = itemType;
		this.itemClass = itemClass;
		this.organization = organization;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public DefItemTypeEnum getItemType() {
		return itemType;
	}

	public void setItemType(DefItemTypeEnum itemType) {
		this.itemType = itemType;
	}

	public DefItemClassEnum getItemClass() {
		return itemClass;
	}

	public void setItemClass(DefItemClassEnum itemClass) {
		this.itemClass = itemClass;
	}

	public Integer getFirst() {
		return first;
	}

	public void setFirst(Integer first) {
		this.first = first;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getCodeLike() {
		return codeLike;
	}

	public void setCodeLike(String codeLike) {
		this.codeLike = codeLike;
	}

	public String getNameLike() {
		return nameLike;
	}

	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}

}
