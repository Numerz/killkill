package cn.wxn.killkill.model.mapper;

import cn.wxn.killkill.model.entities.RandomCode;
import cn.wxn.killkill.model.entities.RandomCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RandomCodeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    long countByExample(RandomCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int deleteByExample(RandomCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int insert(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int insertSelective(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    List<RandomCode> selectByExample(RandomCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    RandomCode selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int updateByExampleSelective(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int updateByExample(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int updateByPrimaryKeySelective(RandomCode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table random_code
     *
     * @mbg.generated Mon Aug 17 20:31:24 CST 2020
     */
    int updateByPrimaryKey(RandomCode record);
}