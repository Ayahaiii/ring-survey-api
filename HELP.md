动态表操作规范
    /**
     * 创建样本呢表
     * @param projectId
     * @return
     */
    public int createSampleTable(Integer projectId) {
        return sampleDao.createCustomTable(ProjectConstants.getSampleTableName(projectId));
    }

    /**
     * 添加数据
     * @return
     */
    public int insertSample() {
        BaseProjectSample sample = new BaseProjectSample();
        sample.setDynamicTableName(ProjectConstants.getSampleTableName(1));
        return sampleDao.insert(sample);
    }

    /**
     * 批量添加数据
      * @return
     */
    public int insertSampleList() {
        // 只需要将第一条数据添加表名
        List<BaseProjectSample> samples = new ArrayList<>();
        BaseProjectSample sample = new BaseProjectSample();
        sample.setDynamicTableName(ProjectConstants.getSampleTableName(1));
        samples.add(sample);
        return sampleDao.insertList(samples);
    }

    /**
     * 修改数据
     * @return
     */
    public int updateSample() {
        BaseProjectSample sample = new BaseProjectSample();
        sample.setDynamicTableName(ProjectConstants.getSampleTableName(1));
        return sampleDao.updateByPrimaryKey(sample);
    }

    /**
     * 获取数据
     * @return
     */
    public BaseProjectSample getSample() {
        Example example = new Example(BaseProjectSample.class);
        example.setTableName(ProjectConstants.getSampleTableName(1));
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", 1);
        return sampleDao.selectOneByExample(example);
    }