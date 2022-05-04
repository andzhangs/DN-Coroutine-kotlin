package com.project.coroutines.mapper

import com.project.coroutines.db.entity.ProjectEntity
import com.project.coroutines.model.DataX
import com.project.coroutines.model.ProjectItemModel

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
class EntityToModelMapper : Mapper<ProjectEntity, ProjectItemModel> {
    override fun map(input: ProjectEntity): ProjectItemModel {
        return ProjectItemModel(id = input.id, author = input.author, icon = input.envelopePic)
    }
}