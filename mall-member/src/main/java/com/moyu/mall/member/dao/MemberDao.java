package com.moyu.mall.member.dao;

import com.moyu.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:37:48
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
