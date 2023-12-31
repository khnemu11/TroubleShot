package com.orientalSalad.troubleShot.troubleShooting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

import com.orientalSalad.troubleShot.troubleShooting.dto.CategoryDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.SearchTroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingAnswerDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingAnswerReplyDTO;
import com.orientalSalad.troubleShot.troubleShooting.dto.TroubleShootingDTO;
import com.orientalSalad.troubleShot.troubleShooting.sql.TroubleShootingSQLProvider;

@Mapper
public interface TroubleShootingMapper {
	public TroubleShootingDTO selectTroubleShootingBySeq(@Param("searchParam") SearchTroubleShootingDTO searchParam);
	public TroubleShootingAnswerDTO selectTroubleShootingAnswerBySeq(@Param("seq") Long seq);
	public TroubleShootingAnswerReplyDTO selectTroubleShootingAnswerReplyBySeq(@Param("seq") Long seq);

	public List<TroubleShootingDTO> selectTroubleShootingList(
		@Param("searchParam") SearchTroubleShootingDTO searchParam,
		@Param("tags") List<String> tags,
		@Param("tagSize") int tageSize);

	public void updateTroubleShootingView(@Param("seq") Long seq);

	@SelectProvider(type = TroubleShootingSQLProvider.class, method = "findTroubleShootingListByUserSeq")
	@ResultMap("TroubleShooting")
	public List<TroubleShootingDTO> selectTroubleShootingListByUserSeq(
		@Param("searchParam") SearchTroubleShootingDTO searchParam,
		@Param("userSeq") Long userSeq);

	public Long countTroubleShootingList(
		@Param("searchParam") SearchTroubleShootingDTO searchParam,
		@Param("tags") List<String> tags,
		@Param("tagSize") int tageSize);

	@SelectProvider(type = TroubleShootingSQLProvider.class, method = "countTroubleShootingListByUserSeq")
	public long countTroubleShootingListByUserSeq(
		@Param("searchParam") SearchTroubleShootingDTO searchParam,
		@Param("userSeq") Long userSeq);

	public void updateCategory(
		@Param("category") CategoryDTO category,
		@Param("original") String original);
}
