# 项目实战

## 第一步：Hilt负责托管对象与对象之间的注入关系

1. @HiltAndroidApp：触发hilt代码生成
2. @AndroidEntryPoint：创建一个依赖容器，该容器遵循Android类的生命周期
3. @Module：告诉Hilt如何提供不同类型的实例
4. @InstallIn：Install用来告诉Hilt这个模块会被安装到哪个组件上
5. @Providers：告诉Hilt如何获得具体的实例
6. @Singleton：单例
7. @ViewModelInject：通过构造函数，给ViewModel注入实例
