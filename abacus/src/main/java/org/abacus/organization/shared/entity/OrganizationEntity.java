package org.abacus.organization.shared.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.abacus.common.shared.entity.StaticEntity;
import org.abacus.definition.shared.constant.EnumList;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@SuppressWarnings("serial")
@Table(name = "org_organization")
public class OrganizationEntity extends StaticEntity {

	@Column(name = "name", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "level_enum", nullable = false)
	private EnumList.OrgOrganizationLevelEnum level = EnumList.OrgOrganizationLevelEnum.L0;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id")
	private OrganizationEntity parent;

	@OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
	@Fetch(FetchMode.SELECT)
	private Set<FiscalYearEntity> fiscalYearSet;

	public OrganizationEntity(String id) {
		this.id = id;
	}

	public OrganizationEntity() {

	}
	
	public List<OrganizationEntity> getParentList(){
		OrganizationEntity orgEntity = this;
		List<OrganizationEntity> list = new ArrayList<>();
		list.add(orgEntity);
		int currentLevelIndex = this.getLevel().ordinal();
		int requestLevelIndex = 0;
		while (requestLevelIndex < currentLevelIndex) {
			orgEntity = orgEntity.getParent();
			list.add(orgEntity);
			requestLevelIndex++;
		}
		return list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EnumList.OrgOrganizationLevelEnum getLevel() {
		return level;
	}

	public void setLevel(EnumList.OrgOrganizationLevelEnum level) {
		this.level = level;
	}

	public OrganizationEntity getParent() {
		return parent;
	}

	public void setParent(OrganizationEntity parent) {
		this.parent = parent;
	}

	public Set<FiscalYearEntity> getFiscalYearSet() {
		return fiscalYearSet;
	}

	public void setFiscalYearSet(Set<FiscalYearEntity> fiscalYearSet) {
		this.fiscalYearSet = fiscalYearSet;
	}

}
