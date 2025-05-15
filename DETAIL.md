## 一、项目介绍

智云星课是一款基于Vue3 + React + Springboot + Redis + MyBatis + MyBatisPlus + ElasticSearch + Dify + 扣子 + 文多多AI PPT + SSE + knife4j + hutool + okhttp + netty + websocket+ Django + PaddlePaddle + PP-yolo + Ant Design Pro + AntDesign + VantUI + FirstUI + Uni-app的集成AI功能的一站式教学辅助的手机端h5网页客户端及微信小程序

基本功能：

- 公告模块
- 智慧体模块
- 课程模块
- 每日单词模块
- 每日美文模块
- 交友模块
- 圈子模块
- 错题本模块
- 练习题模块
- 电子书模块
- 口语练习模块
- 图片生成模块
- 搜索模块
- 个人信息模块
- 个人学习计划模块
- 个人成就模块
- 最近学习模块
- 用户反馈模块
- 会员权益模块
- 支付模块
- 统计分析模块

创新点：

1. 学生能够借助人工智能技术迅速获取全面的知识体系，个性化定制学习计划，并获得详尽的题目解析。此外，系统还能生成同类型但难度各异的练习题，以帮助学生巩固所学知识。
2. 教师可以根据教材及教学大纲快速制定教学方案，利用模型API自动生成课程PPT。同时，通过分析班级内每位学生的学习表现，教师能够进一步调整和优化教学策略。
3. 在硬件方面，我们选用了香橙派AIPro设备，结合PP-Yolo模型并通过昇腾NPU进行高效计算，实现了对学生学习状态的即时识别。这使得教师即使处于远程在线环境中，也能够及时了解学生的学习状况并给予必要的指导与提醒。

## 二、市场调研

- **同类产品参考**

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777824981-0b38688c-20f7-4ec6-a328-9cc29d91a318.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825021-db17e3c1-a8b0-48db-ac3a-56efc4e5c17a.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825052-17af8a7e-dfe0-452e-b03f-f4ef89d64787.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825068-737a8341-90a9-4812-9ea8-94cdc2f03096.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777824971-ebdb3b0b-5b78-4029-9c25-1f0d017eb6d7.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825798-867543be-42d9-4e3e-9760-7a0116efd6c3.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825926-b140970b-12e1-4af6-b593-b26a5777985d.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825921-ec5c692d-448a-41fd-b8f7-8f383ec511d0.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825772-43f7647d-8e21-43e4-a546-b9b54b880ec5.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777825995-4ecce122-15f9-49ef-b3bc-391ce22e897a.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777826462-3b881c51-3a36-4e72-8a6a-b6e13fda97af.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777826546-28a675d5-b242-4d4a-957d-d24bdcf8c376.jpeg)![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741777826359-c9d55adf-7927-4fb8-90e8-c189edcf7dff.jpeg)

### 行业痛点

在线教育在经历疫情催化后已成为教育体系的重要组成部分，但当前在线课堂仍面临**学生学习状态监控困难**、**教师备课效率低下**以及**教育资源分配不均**等**三大核心痛点**。"智课创翼"项目依托PaddlePaddle深度学习框架和Dify统一开发平台，通过整合大型语言模型(LLM)与计算机视觉技术，针对性地提出了创新解决方案。本项目不仅能够提升教学效率与质量，优化课堂互动体验，更能推动教育资源均衡发展，具有重要的教育实践价值和社会意义。

### 学生学习状态实时监控难题的破解之道

1. 在线教育最突出的挑战之一是**学生参与度难以把控**。传统线下课堂中，教师可以通过眼神接触、面部表情和肢体语言直观感知学生的学习状态，而虚拟环境使这些关键信号几乎完全消失。研究表明，超过60%的在线学习者会出现注意力分散问题，且由于缺乏及时干预，知识漏洞会不断累积。"智课创翼"项目创新性地采用计算机视觉技术解决这一痛点。
2. 项目利用PaddlePaddle框架开发的**视觉分析系统**能够实时捕捉并解析学生在课堂中的微表情、眼球运动、姿态变化等非语言信号。通过深度学习算法，系统可以精准识别出注意力涣散（如频繁低头、视线偏离屏幕）、困惑（如皱眉、摇头）或理解（如点头、微笑）等学习状态。这些分析结果以直观的仪表盘形式呈现给教师，包括全班注意力集中度曲线、个体参与度评分和实时预警提示。当系统检测到超过30%的学生出现注意力下降时，会自动提醒教师调整教学节奏或插入互动环节。
3. **情感计算技术**的引入进一步丰富了状态监控的维度。系统能识别七种基本情绪状态，帮助教师了解学生对当前教学内容的情绪反应。例如，当多数学生表现出困惑或厌倦时，教师可及时转换讲解方式；而当检测到兴奋和投入时，则可适当深化相关内容。烟台市在智能化教学创新示范应用中已证实，此类技术能使教师教学调整的精准度提升40%，学生课堂参与度提高35%。
4. 为保障学生隐私，项目采用**边缘计算（支持昇腾）**方案，所有图像处理均在本地设备（香橙派AI Pro）完成，仅上传分析后的元数据。同时，系统支持匿名化模式，教师只能查看聚合数据而非个体影像，有效解决了在线教育中普遍存在的数据隐私顾虑。

### 教师备课与教学效能的智能化提升

1. 教师备课负担过重是在线教育面临的第二大痛点。传统备课模式下，教师需要花费大量时间查阅资料、设计活动并制作课件，而在线教学还额外需要考虑技术适配问题。研究表明，教师平均每周需投入10-15小时进行备课，其中约30%的时间耗费在资源搜索和格式调整上。"智课创翼"项目通过深度整合LLM技术，为这一难题提供了突破性解决方案。
2. 项目的**智能备课系统**基于Dify平台以及扣子平台构建，支持教师通过自然语言输入教学主题、目标学生群体和核心要求，系统即可在分钟内生成完整的课程大纲、分层教学目标、教学活动设计和评估方案。与通用型AI工具不同，该系统内嵌了教育学理论和课程标准，确保输出内容既创新又符合教学规范。
3. 针对在线教学特点，系统提供**多媒体资源智能匹配**功能。当教师确定教学主题后，系统会自动从授权资源库中筛选适配的视频、动画、互动模拟和案例素材，并按教学逻辑进行排序。这些资源均已通过教育专家审核，避免了网络资源质量参差不齐的问题。
4. 项目还创新性地开发了**差异化教学设计引擎**。系统通过分析班级历史学习数据，自动识别学生群体的知识基础、学习风格和能力分布，据此生成多版教学设计方案，教师可根据实际需要灵活选用或融合多种方案，实现真正的因材施教。

1. *表："智课创翼"智能备课系统功能对比传统模式*

| **功能维度**   | **传统备课模式**         | **智课创翼系统**     | **效能提升**  |
| -------------- | ------------------------ | -------------------- | ------------- |
| **大纲生成**   | 手动整理，3-4小时        | AI自动生成，5分钟    | 效率提升97%   |
| **资源搜集**   | 多平台搜索，2-3小时      | 智能匹配，即时完成   | 时间节省100%  |
| **差异化设计** | 依赖教师经验，难以系统化 | 基于数据分析自动生成 | 覆盖率达90%   |
| **技术适配**   | 额外调整在线教学元素     | 内嵌在线教学最佳实践 | 适配度提高80% |

### 促进教育均衡发展的技术路径

1. 教育资源分布不均，尤其是偏远地区优质教育资源匮乏，是在线教育需要解决的第三大痛点。尽管在线教育理论上能够突破地理限制，但由于基础设施不足、师资力量薄弱和技术应用能力欠缺，偏远地区学生仍难以获得与城市同质的教育体验。"智课创翼"项目通过技术创新和模式创新，为促进教育公平提供了可行路径。
2. 项目的**轻量化部署方案**专门针对基础设施薄弱地区设计。系统核心功能可在低至2Mbps的网络环境下流畅运行，视频传输采用腾讯云优化COS对象存储，通过CDN技术，将延迟控制在200ms以内，适应高延迟、低带宽的网络条件。界面设计充分考虑低数字素养用户需求，提供大字体、简操作，使技术门槛大幅降低。
3. 为解决偏远地区师资不足问题，项目开发了**AI辅助教学系统**。该系统不仅能提供知识点讲解和答疑，更能基于LLM的推理能力，引导学生进行探究式学习。例如，当学生提出"为什么天空是蓝色的"这类问题时，系统不会直接给出答案，而是通过一系列引导性问题帮助学生自主发现瑞利散射原理。目前测试表明，这种启发式教学的效果已接近普通教师水平，尤其适合师资短缺地区的基础教育阶段。
4. 为保障项目可持续发展，团队设计了**分层服务模式**。基础版包含核心教学功能，向教育资源薄弱地区免费开放；高级版增加数据分析、个性化推荐等进阶功能，面向有条件地区提供适度收费服务。这种模式既确保了教育普惠性，又为项目持续迭代提供了资金支持。联合国教科文组织与编程猫合作的类似项目已证明，这种"技术普惠+商业可持续"的双轨模式具有广泛推广价值。

## 三、系统功能设计

### 项目功能梳理

### 需求优先级

根据核心业务业务流程，明确需求开发的优先级。

- P0 为核心，非做不可
- P1 为重点功能，最好做
- P2 为实用功能，有空就做
- P3 可做可不做，时间充裕再做

X4AGTE7ec5E7RO0ExoQq3s9FwsNM5imNi

按照模块梳理功能：

### 用户模块

1. **用户注册**：提供新用户创建账户的功能。
2. **用户登录**：允许已注册用户通过验证其身份信息进入系统。
3. **个人信息更新**：使用户能够修改自己的基本信息，如联系方式、密码等。
4. **个人资料展示页面**：为每位用户提供一个展示其个人信息的界面。
5. **用户管理**（限管理员）：包括对用户账号进行添加、删除、编辑及查询的操作权限。

### 公告模块

1. **创建公告**：允许具有相应权限的用户（如管理员）撰写并发布新的公告。在创建过程中，可以设置公告的内容、标题、发布时间以及有效期等信息。
2. **修改公告**：对于已发布的公告，如果需要更新其内容或调整相关信息，可以通过此功能进行编辑。这包括但不限于更改公告的文字内容、链接、图片或其他媒体文件等。
3. **删除公告**：当某个公告不再有效或需要被移除时。但需要注意的是，在执行删除操作前最好确认。
4. **查看公告列表**：所有用户都可以访问到一个包含当前所有有效公告的列表页面。这个列表通常会按照发布时间排序，并且提供搜索和筛选功能以方便查找特定类型的公告。
5. **查看公告详情**：点击公告列表中的任意一条记录后，将跳转至该公告的详细页面，在这里可以看到完整的公告内容以及其他附加信息，比如发布者、发布时间等。
6. **管理公告 - 增删改查（仅管理员可用）**：这是一个高级管理界面，专门为管理员设计，使得他们能够全面地控制整个公告系统的运作。通过这一界面，管理员不仅可以实现上述提到的所有基本操作，还可以批量处理多条公告记录，设置公告的可见范围等更复杂的任务。
7. **公告已读状态，确认查收**：为了确保重要信息能够被及时传达给每一位相关人士，系统还提供了“已读”标记功能，系统自动检测用户的浏览行为并在一定条件下自动完成标记。此外，对于某些特别重要的通知，还可以要求接收者必须点击确认按钮来证明他们已经收到了这条消息。

### 智慧体模块

1. **创建智慧体**：用户可以根据自己的需求，通过定义名称、描述以及配置相关参数来创建一个新的智慧体。
2. **修改智慧体**：一旦智慧体被创建后，如果需要调整其属性或者更新信息，用户可以利用此功能进行修改。这包括但不限于更改智慧体的名字、描述等基本信息，也可能涉及更深层次的功能调整。
3. **删除智慧体**：当某个智慧体不再需要时，用户可以选择将其从系统中移除。
4. **查看智慧体列表**：为了方便管理和浏览所有已存在的智慧体，系统提供了查看智慧体列表的功能。这里会列出每个智慧体的基本信息概览，帮助用户快速定位到感兴趣的项目。
5. **查看智慧体详情**：除了简单的列表展示外，用户还可以点击进入任意一个智慧体的详细页面，获取更加全面的信息，比如详细prompt，个性，对话演示，工具支持以及其他重要设置等。
6. **查看自己创建的智慧体**：为了让用户更容易地管理自己负责的内容，系统特别设置了这样一个视图，只显示由当前登录账号所创建的所有智慧体。
7. **管理智慧体 - 增删改查（仅管理员可用）**：对于拥有更高权限级别的管理员来说，他们能够对整个平台上的所有智慧体进行全面的操作，包括增加新的条目、删除不需要的数据、修改现有记录以及查询相关信息等。
8. **审核发布和下架智慧体（仅管理员可用）**：同样属于高级别权限范围内的功能之一，管理员有权决定哪些智慧体可以对外公开使用，哪些则需要暂时隐藏起来不再提供服务。这是确保内容质量与安全性的关键步骤。
9. **智慧体分享（扫码查看）**：为了促进知识的传播与交流，系统支持通过二维码的形式将某个智慧体的信息分享出去。接收方只需扫描二维码即可直接访问到对应的智慧体详情页，无需手动输入网址或其他复杂操作。

### 课程模块

1. **创建课程**

1. 用户可以创建新的在线课程，包括设定课程名称、描述、目标群体等基本信息。此外，还可以设置课程的结构，比如章节划分、学习路径规划、资料等，以确保学生能够按照逻辑顺序进行学习。

1. **修改课程**

1. 对于已经发布的课程，教师或内容创作者可以根据需要调整课程内容，如更新教学材料、修改课程描述或调整章节顺序等。这有助于保持课程内容的新鲜度和相关性。

1. **删除课程**

1. 当某个课程不再被需要时，可以通过删除功能将其从系统中移除。

1. **导入课程信息**

1. 支持批量导入课程的基础信息，例如使用CSV文件格式快速添加多门课程的基本详情。这对于大规模部署新课程非常有用，可以大大节省手动输入的时间。

1. **导入课程视频**

1. 允许将预先录制好的视频直接上传到特定课程下，方便学生随时观看复习。视频文件应符合平台规定的格式要求，并且建议在上传前做好质量检查。

1. **导入资料**

1. 除了正式的教学视频外，还可以为每门课程附加额外的学习资源，如实验演示、案例分析、文字资料等辅助资料。这些补充材料有助于加深理解，提高学习效果。

1. **直播模块**

1. **直播准备**：讲师可以提前安排好直播时间表，并通过平台发送通知给已注册的学生。
2. **互动功能**：直播过程中支持实时问答、投票等多种形式的师生互动，增强课堂参与感。
3. **录像回放**：可以手动保存每次直播的内容，以便未能参加现场直播的同学日后查看。

1. **录播模块**

1. **播放控制**：学生端拥有灵活的播放选项，如快进/倒退、倍速播放等，以适应不同学习习惯。
2. **笔记功能**：内置笔记记录器，让学生能够在观看视频的同时做笔记，便于复习巩固知识点。

1. **管理课程 - 增删改查（仅管理员可用）**

1. **增加**：管理员有权新增任何类型的课程至数据库中。
2. **删除**：同样地，他们也可以永久移除不再适用的课程条目。
3. **修改**：对现有课程的信息进行更改，包括但不限于标题、简介等内容。
4. **查询**：快速检索特定课程的状态及其相关信息，便于管理和维护整个课程体系。

### 统计分析模块

1. **智慧体评分结果展示**

1. **定义**：此部分展示了基于特定评价标准或算法计算出的智慧体表现得分。这些分数可以反映智慧体在完成任务时的有效性、准确性以及用户体验等方面的表现。
2. **用途**：通过对不同时间段内智慧体评分变化趋势的观察，可以帮助开发者识别出需要改进的地方，并据此调整训练策略或算法模型。
3. **展示方式**：通常采用图表形式（如折线图、柱状图）来直观地表示评分随时间的变化情况；也可以通过列表形式列出具体评分数值及其对应日期。

1. **智慧体数量展示**

1. **定义**：这部分提供了系统中当前活跃或者注册过的智慧体总数信息。
2. **重要性**：了解智慧体的数量对于评估系统的规模及增长潜力至关重要。同时，在某些应用场景下，合理控制智慧体数量有助于提高资源利用率和服务质量。
3. **展示方法**：一般以数字直接显示当前智慧体总数，并可能结合历史数据生成趋势图，以便于跟踪一段时间内的增长或减少情况。

1. **智慧体日活展示**

1. **定义**：这里指每日实际使用过至少一次服务的智慧体数量统计。
2. **价值**：日活跃用户数是衡量产品受欢迎程度及用户粘性的重要指标之一。对于智慧体而言，高日活意味着更高的互动频率和更广泛的影响力。
3. **呈现形式**：除了基本的日活总数外，还可以通过日历热力图等形式展现不同日期间日活量的变化，便于发现节假日效应或其他特殊事件对日活的影响。
   综上所述，统计分析模块通过上述三个方面——评分结果、数量以及日活情况——为用户提供了一个全方位了解智慧体运行状况的窗口，从而支持更加科学合理的管理和优化工作。

1. **课程评分展示**

1. **定义**
   此部分展示学习者对课程内容、教学质量及体验的综合评分结果。评分可基于学员反馈表、课后测验成绩或系统自动评估（如完成度、互动频率）生成，反映课程设计的合理性和用户满意度。
2. **用途**
   通过分析课程评分，教育管理者可识别优质课程或需优化的薄弱环节，例如调整教学节奏、补充资源或改进互动设计。横向对比不同课程评分还能辅助资源分配决策。
3. **展示方式**
4. **可视化对比**：使用柱状图或雷达图对比多门课程的综合评分（如知识深度、趣味性、实用性等维度）；
5. **时间趋势**：折线图展示单门课程评分的长期变化，关联课程迭代效果；
6. **详情列表**：按评分高低排序课程列表，支持筛选和查看具体评价内容。

1. **课程日活展示**

1. **定义**
   统计每日主动访问或参与课程学习的独立用户数（如观看视频、提交作业、参与讨论等行为），衡量课程的实际使用频率和用户黏性。
2. **重要性**

1. 日活数据可反映课程的即时吸引力和长期价值：
2. 高日活表明课程内容符合用户需求或运营活动有效；
3. 异常波动可能关联课程更新、推广策略或外部事件（如考试季）。

1. **呈现形式**

1. **趋势图**：折线图展示近7/30天日活变化，标注关键事件（如新课上线、促销活动）；
2. **热力图**：按周/月维度展示每日活跃分布，识别周末/工作日的使用习惯差异；
3. **排行榜**：按日活高低对课程分类排名，突出热门课程或潜力内容。

1. **学习活跃状态展示**

1. **定义**
   实时或周期性统计学习者的在线状态、互动行为（如提问、笔记、测验提交）及进度完成情况，动态反映整体或个体的学习投入度。
2. **价值**

1. **个体层面**：识别高活跃度学员（如潜在优秀贡献者）或需督促的低活跃用户；
2. **群体层面**：分析学习高峰期、常用功能模块，优化服务器资源分配或功能优先级；
3. **教学干预**：针对“中途停滞”用户自动触发提醒机制，提升课程完成率。

1. **展示方法**

1. **实时看板**：仪表盘显示当前在线人数、瞬时互动量（如讨论区发帖数）；
2. **行为热力图**：按小时/天展示用户活跃时间段，辅助制定推送策略；
3. **进度分布图**：环形图展示学员进度比例（如“已完成20%”、“中途退出”占比）；
4. **个性化标签**：为学员标记活跃等级（如“积极”“普通”“待激活”），支持定向运营。

### 每日单词模块

1. **创建单词**：用户可以自由地添加新的词汇到自己的词库中，这不仅包括基本的拼写信息，还可以附加定义、例句、同义词、反义词等详细内容，以便更全面地掌握每个单词。
2. **修改单词**：如果用户发现某个已存单词的信息有误或者想要更新其相关资料（如增加更多例句或修改解释），可以通过此功能轻松实现。此外，当用户的语言水平提高后，他们也可以根据最新的学习需求调整单词的学习级别或其他属性。
3. **删除单词**：对于那些不再需要记忆或已经完全掌握的词汇，用户可以选择将其从个人词库中移除，以保持词库的整洁并专注于新知识的学习。
4. **查看单词列表**：用户能够按照不同的标准（比如按字母顺序排列、按难易程度分类等）浏览自己收藏的所有单词，并且支持搜索功能来快速定位特定词汇的位置。
5. **发布每日单词**：系统会自动为用户挑选出一个或多个适合当天学习的新单词，并通过邮件、短信等形式发送给用户，帮助他们每天都能接触到新鲜的知识点。
6. **自定义导入单词信息**：允许用户批量上传自己整理好的单词表，格式可以是Excel文件、文本文件等多种形式，从而大大节省了手动输入的时间和精力。
7. **制定单词计划**：用户可以根据自身情况设定合理的学习目标，比如每周学习多少个新单词，复习哪些旧单词等，系统将依据这些设置生成个性化的学习日程安排，辅助用户更加高效有序地进行英语词汇积累。

### 交友模块

1. **发送好友请求**

1. 用户可以通过搜索到的用户资料页面或通过扫描二维码的方式向其他用户发送添加好友的请求。此功能允许用户自定义附带的消息，以增加个性化沟通的机会。

1. **搜索好友**

1. 提供多种方式帮助用户找到潜在的好友，包括但不限于通过用户名、手机号码或者邮箱地址进行搜索。此外，还支持基于共同兴趣、地理位置等条件来匹配可能感兴趣的新朋友。

1. **扫一扫支持**

1. 利用摄像头扫描功能，用户可以快速地通过二维码识别并添加新朋友。这种便捷的方式特别适合面对面交流时使用，大大简化了加好友的过程。

1. **接受好友申请**

1. 当收到新的好友请求时，系统会通知用户，并在专门的界面展示所有待处理的申请列表。用户可以选择接受或拒绝这些请求，并且对于不希望继续联系的人还可以选择屏蔽其未来的所有消息。

1. **展示好友列表**

1. 为用户提供一个清晰有序的好友管理界面，在这里可以看到所有已建立联系的好友以及他们当前的状态（如在线/离线）。用户能够轻松地查看好友信息、发起聊天或调整分组设置。

1. **好友对话**

1. 构建了一个稳定高效的即时通讯平台，让好友之间可以无延迟地收发文本消息。除了基本的文字交流外，该模块还集成了表情包、贴纸等功能，使沟通更加生动有趣。

1. **图片信息支持**

1. 用户可以在聊天过程中直接发送图片文件给对方，无论是分享生活点滴还是工作中的重要资料都非常方便。同时，为了保护隐私安全，还提供了图片预览限制和过期删除选项。

1. **视频信息支持**

1. 除了静态图像外，视频也是现代社交不可或缺的一部分。此功能允许用户录制短视频或从相册中选择已有视频发送给好友，极大地丰富了表达形式。

1. **常用文档支持**

1. 考虑到工作中经常需要交换各种类型的文档，我们特别增加了对Word、Excel、PDF等多种格式文件的支持。用户可以直接在应用内打开查看这些文档，无需额外下载任何软件。

1. **发送语音**

1. 当打字变得繁琐时，用户可以选择录制一段语音留言代替文字输入。这不仅节省时间，还能更好地传达语气与情感。

1. **语音转文字**

1. 针对那些更喜欢阅读而非听音频的用户，我们提供了先进的语音识别技术，能够将接收到的语音自动转换成文字显示出来。这样既保留了原始声音信息，又满足了不同用户的偏好需求。

### 圈子模块

1. **发送帖子**：用户可以通过这个功能在圈子内发布自己的想法、观点、照片或视频等内容。这有助于促进用户的互动，并让其他成员了解最新的动态。

2. **删除帖子**：如果用户希望移除自己之前发布的某个内容，可以使用此选项来彻底删除该条目。这样能够帮助维护个人隐私或者清理不再需要的信息。

3. **修改帖子**：对于已经发布的帖子，如果发现有错误或是想要更新内容时，用户可以选择编辑功能进行相应调整。这项特性保证了信息的准确性和时效性。

4. **点赞帖子**：通过给喜欢的内容点赞，用户不仅可以表达自己对该内容的认可和支持，同时也增加了作者的积极性。点赞数往往也是衡量一条内容受欢迎程度的重要指标之一。

5. **已读帖子**：标记为已读后，系统会记录下用户浏览过哪些帖子，方便后续追踪未阅读的新消息。这对于保持圈子里信息流动畅通非常重要。

6. **评论帖子**：除了简单的点赞外，用户还可以对感兴趣的帖子发表更深入的看法或提出问题，从而引发更多讨论。评论区往往是思想碰撞最激烈的地方之一。

7. **分享帖子**：将有价值的内容转发到其他社交网络或直接分享给朋友，可以帮助扩大其影响力。同时，这也是一种推荐好内容的方式。

8. **帖子评分排序**：根据用户给予的评价（如点赞数量、评论活跃度等），系统会对所有帖子进行排名展示。这种方式可以让最受欢迎的内容更容易被发现。

9. **跳转个人信息**：点击用户名或其他相关链接后，可以直接访问到该用户的个人主页，在那里可以看到更多关于他们的详细资料以及他们所参与过的活动记录等信息。

   

### 错题本模块

1. **题目录入**：允许用户手动输入或通过拍照、扫描等方式快速添加错题。
2. **自动分类与标签**：根据学科、知识点等自动对错题进行分类，并支持用户自定义标签。
3. **解析与答案查看**：提供详细的解题步骤和正确答案，帮助理解错误原因。
4. **收藏与备注**：用户可以将重要的错题收藏起来，并为每道题添加个人笔记或注释。
5. **搜索功能**：支持通过关键词、标签等多种方式快速查找特定的错题。
6. **复习提醒**：基于艾宾浩斯遗忘曲线或其他记忆理论设置复习计划，并提醒用户按时复习。
7. **进度跟踪**：记录用户的练习情况，如完成度、正确率等，以便于自我评估学习效果。
8. **分享与交流**：允许用户将自己的错题本分享给他人，或是参与社区讨论，互相学习。
9. **个性化推荐**：根据用户的错题类型及学习习惯推荐相关练习题或资料。
10. **安全备份**：定期自动备份错题本内容，防止因意外丢失重要信息。
    这些功能旨在提高学习效率的同时也增加了使用的便捷性和趣味性。

### 练习题模块

1. **题目分类与标签**：根据学科、年级、难度等维度对题目进行分类，并允许设置自定义标签，方便用户快速找到所需题目。
2. **智能推荐系统**：基于用户的学习历史和表现自动推荐适合的练习题目，支持个性化学习路径规划。
3. **在线作答与即时反馈**：

- 支持多种类型的题目（单选题、多选题、填空题、简答题等）。
- 实现自动评分机制，对于选择题等客观题型可以立即给出答案及解析；对于主观题，则提供给老师批改的功能。
- 提供详细的解题步骤展示，帮助学生理解错题原因。

1. **成绩记录与分析报告**：

- 记录每次练习的成绩数据。
- 生成个人或班级层面的成绩统计图表，如正确率趋势图、薄弱知识点分布等。
- 根据成绩分析结果提出针对性的学习建议。

1. **互动交流区**：设立讨论板块，鼓励学生之间就难题进行探讨交流，也可以邀请老师参与答疑解惑。
2. **家长/教师管理端口**：为家长或教师提供查看孩子/学生练习情况的权限，包括完成进度、得分情况等信息，并能发送提醒或鼓励消息。

### 电子书模块

1. **电子书浏览与搜索**

1. **分类浏览**：按学科、年级、出版社等分类展示电子书，便于快速找到所需资源。
2. **关键词搜索**：支持书名、作者、关键词搜索，提升查找效率。
3. **热门推荐与最新上架**：增加用户发现新资源的机会，提升使用体验。

1. **电子书借阅与管理**

1. **在线借阅与归还**：方便用户使用，避免实体书籍的管理问题。
2. **在线阅读与下载**：适应不同场景需求，随时随地学习。
3. **借阅期限与提醒**：公平分配资源，避免长时间占用。

1. **阅读体验优化**

1. **简洁界面设计**：提升用户体验，操作直观简便。
2. **多格式支持**：兼容PDF、EPUB等多种格式，满足不同需求。
3. **阅读设置**：字体调整、背景颜色、夜间模式等，提升舒适度。

1. **笔记与标注功能**

1. **批注与标记**：方便学习重点，便于复习。
2. **云存储同步**：在多设备间轻松同步笔记。
3. **分享功能**：促进学习交流，分享学习成果。

1. **互动与社交功能**

1. **评论与问答**：增加互动性，促进师生互动。
2. **学习小组与讨论区**：提高学习动力，组织学习活动。

1. **资源管理功能**

1. **上传与下载**：方便教师分享资源，灵活管理。
2. **分类管理与标签**：有序管理资源，提升查找效率。
3. **版本控制与更新**：确保资源及时更新，避免过时。

1. **统计与分析工具**

1. **阅读统计**：跟踪阅读时长与进度，帮助自我评估。
2. **借阅统计与资源分析**：优化资源管理，提升利用率。

1. **安全与权限管理**

1. **用户权限控制**：确保资源安全，合理分配访问权限。
2. **加密与水印**：防止盗版和滥用，保护版权。
3. **内容审查**：过滤不良信息，确保内容适宜。

### 口语练习模块

1. **语音识别与评估**：通过先进的语音识别技术，能够准确地理解学习者的发音，并根据标准发音模型来评估其准确性。这不仅限于单词或短语的发音正确与否，还包括语调、连读等更细微的语言特征。
2. **实时反馈**：基于上述评估结果，即时给予用户关于他们发音质量的反馈。这种反馈可能是文本形式的建议，也可能是通过合成语音来示范正确的发音方式。
3. **情景对话模拟**：创建各种日常生活场景下的对话练习，让学习者能够在接近真实交流的情境中实践所学知识。AI可以根据用户的回答自动生成下一句对话内容，使得整个过程更加流畅自然。
4. **个性化学习路径**：根据每个学生的学习进度和能力水平调整教学内容难易程度，确保每个人都能在适合自己的节奏下进步。此外，还可以推荐特定领域的词汇或语法点以供深入学习。
5. **文化背景介绍**：除了语言技能外，还应涵盖目标语言所在国家或地区的基本文化信息，帮助学生更好地理解和适应不同的社交环境。
6. **游戏化元素**：将学习过程设计成有趣的游戏任务，比如角色扮演、解谜挑战等，以此激发学生的兴趣并提高参与度。

### AI图片生成模块

1. **文本到图像转换**：这是最基本也是最核心的功能，用户可以通过输入一段文字描述来生成相应的图片。这有助于将抽象的概念具体化，提高学生的理解和记忆效率。
2. **多风格支持**：能够根据不同的教学需求或个人偏好选择不同的艺术风格（如卡通、写实等）来生成图片，增加趣味性和多样性。
3. **教育内容定制**：针对特定学科领域（数学、物理、化学、历史等）提供专门优化过的图像生成服务，确保生成的图片与教学内容高度相关且准确无误。
4. **互动式学习材料创建**：允许教师和学生共同参与创作过程，比如通过添加注释、标签等形式使图片成为更加丰富有效的学习资源。

### 搜索模块

1. **多维度搜索**：允许用户根据不同的标准进行搜索，比如按课程名称、授课老师、学科分类、年级水平等。这样可以帮助用户快速找到他们感兴趣的或需要的信息。
2. **全文检索**：支持对文档内容的全文搜索，包括但不限于课程介绍、教学大纲、课件资料等文本信息。这有助于学生和教师更精确地定位到所需的学习材料或者参考资料。
3. **智能推荐**：基于用户的搜索历史、浏览行为以及个人偏好等因素，向用户推荐相关性强的内容。例如，如果一位学生经常查看物理相关的资料，则系统可以优先展示与物理学有关的新课程或资源。
4. **AI辅助搜题**：利用人工智能技术帮助学生解决作业难题。通过输入问题描述或上传图片形式的问题，AI能够识别并给出解答思路甚至是完整的解题步骤。这对于促进自主学习非常有帮助。
5. **语音搜索**：考虑到部分用户可能更加习惯于使用语音输入的方式来进行查询，因此提供语音搜索选项也是一个不错的选择。特别是在移动设备上，这种交互方式更加自然便捷。
6. **高级筛选与排序**：为用户提供更多自定义设置来优化其搜索结果，比如按照评分高低、发布时间新旧、参与人数多少等方式进行排序；同时也可以设置过滤条件，如只显示免费资源、限定特定格式的文件等。

### 个人学习计划模块

1. **智能课程推荐**：基于用户的学习历史、兴趣偏好以及能力水平等信息，利用AI算法自动推荐适合用户的课程或学习材料。
2. **个性化学习路径规划**：根据每位学生的目标（如考试准备、技能提升等）、现有知识基础及可用时间等因素，通过AI技术生成个性化的学习路线图，并随学习进度动态调整。
3. **智能提醒与进度跟踪**：设置学习目标和截止日期后，系统能够自动发送提醒通知；同时，还可以通过数据分析来监测学生的实际学习情况与预期之间的差距，及时给出建议。
4. **自适应测试与评估**：利用自然语言处理(NLP)等技术进行题目解析，为用户提供即时反馈，并根据答题结果调整后续题目的难度，以更好地匹配学生当前的知识掌握程度。
5. **虚拟助教/导师支持**：开发聊天机器人形式的虚拟助手，可以回答常见问题、解释概念、甚至参与讨论。对于更复杂的问题，则可以连接到真人教师那里获得帮助。
6. **情感分析与心理健康关怀**：通过对用户行为模式的观察以及对社交互动中表达的情绪进行分析，识别出可能存在的压力过大或其他心理问题的学生，并给予适当的支持或者建议他们寻求专业咨询。

### 个人成就模块

1. **成就徽章**：为完成特定课程或达成某些目标（如连续登录天数、完成特定数量的练习等）的用户提供数字徽章。这些徽章不仅能够展示用户的进步，还能作为社交分享的内容。
2. **经验值与等级制度**：根据用户在平台上活动获得的经验值来提升等级。例如，观看视频、完成测验、参与讨论等都可以获得相应经验值。随着等级的提升，用户可以获得更多的特权或者奖励。
3. **排行榜**：展示用户之间基于积分、成就或其他指标的竞争情况。这有助于激发用户之间的健康竞争，同时也增加了社区感。
4. **任务挑战**：设置各种短期或长期的任务挑战，比如每日挑战、每周挑战等，完成挑战后可获得额外奖励。这样既能增加趣味性又能促进用户更加积极地参与到学习中去。
5. **社交互动**：鼓励用户通过论坛、小组等形式与其他学习者交流心得体验。还可以设置一些需要团队合作才能完成的任务，增强用户间的互动性。
6. **成就共享**：提供方便快捷的方式让用户将自己的成就分享到社交媒体上，以此来吸引更多潜在用户加入平台。

### 最近学习模块

1. **学习记录概览**：显示过去30天内用户的总学习时间、完成的课程数量以及参与的不同类型活动（如视频观看、练习题完成等）。
2. **每日/每周学习报告**：为用户提供每天或每周的学习总结，包括学习时长、所学内容摘要等信息。这有助于用户了解自己的学习习惯和偏好。
3. **学习趋势分析**：通过图表等形式展示用户在过去一个月内的学习趋势，比如最活跃的学习时间段、偏好科目等，以便于发现规律并调整学习计划。
4. **社交互动元素**：引入讨论区或小组功能，让学习者能够就共同感兴趣的话题交流心得，形成良好的社区氛围。
5. **提醒与通知服务**：设置自动提醒功能，如即将开始的直播课预告、作业提交截止日期等重要事项提醒，确保不遗漏任何关键信息。
6. **进度追踪工具**：为每门课程提供详细的进度条，清晰地显示出已经完成的部分及剩余任务量，帮助学生更好地管理时间安排。

### 用户反馈模块

1. **反馈分类**：提供选项让用户选择他们想要反馈的问题类型（例如课程内容问题、技术故障、支付问题等），以便于后台快速归类处理。
2. **附件上传**：支持用户上传图片或文件作为反馈的一部分，这对于报告界面错误或其他视觉问题非常有用。
3. **满意度评分**：在适当的情境下收集用户对特定服务或产品的满意度评分，这有助于衡量整体用户体验。
4. **实时聊天支持**：为用户提供即时通讯工具与客服人员沟通的机会，解决紧急问题或获取即时帮助。
5. **进度跟踪**：让用户能够查看自己提交的反馈状态及处理进展，增加透明度。
6. **匿名反馈选项**：给予用户选择是否公开个人信息的权利，鼓励更多人参与进来分享意见。
7. **关键词搜索**：允许用户根据关键词搜索相关反馈记录，方便找到类似问题的解决方案。
8. **反馈分析报告**：后台系统自动生成基于所有收到反馈的数据分析报告，帮助管理层了解主要问题所在，并据此作出改进措施。
9. **自动回复与感谢信**：当接收到新反馈时，系统可以自动发送确认邮件给用户，并表达对他们时间的感激之情。
   这些功能共同作用，不仅能够有效地收集和管理用户的宝贵建议，而且还能增强用户对平台的信任感和满意度。

### 会员权益模块

### 1. 学生端会员权益

- **基础学习资源访问**：包括但不限于视频课程、电子书籍、练习题等。
- **个性化学习计划**：通过AI分析学生的学习习惯和能力水平，为其定制个性化的学习路径。
- **智能答疑助手**：利用AI技术提供24/7在线答疑服务，帮助解答学习过程中遇到的问题。
- **自动笔记生成器**：上课时可以开启此功能记录课堂内容，并自动生成结构化笔记。
- **PPT生成工具**：辅助学生制作报告或演讲所需的PPT材料。
- **社区互动**：建立专属的学习社群，鼓励成员间交流心得体验；同时也可设置专家问答环节，邀请行业专业人士参与讨论。

### 2. 教师端会员权益

- **教学资源库**：提供丰富的教材、教案模板以及多媒体素材供老师使用。
- **AI助教支持**：协助批改作业、评估学生表现，并给出反馈建议。
- **在线教室管理**：包括考勤统计、成绩管理等功能，简化日常行政工作。
- **互动式授课工具**：如实时投票系统、小组讨论室等，增强课堂活跃度。
- **自动化备课助手**：根据课程大纲自动生成讲义、PPT等内容框架。
- **数据分析面板**：收集并展示学生学习进度、知识点掌握情况等关键指标，便于调整教学策略。

### 支付模块

1. **对接多个支付系统**：支持多种支付方式，如信用卡/借记卡支付、第三方支付平台（支付宝、微信支付等）、银行转账等，以满足不同用户的需求。
2. **安全机制**：

1. 加密技术：使用SSL/TLS协议对数据传输过程加密，保护用户的敏感信息不被窃取。
2. 风险控制：通过分析交易模式识别异常行为，防止欺诈交易。
3. 二次验证：对于大额或疑似风险较高的交易，采取额外的身份验证步骤。

1. **订单管理**：能够处理订单的创建、修改和取消；跟踪订单状态直到完成支付流程。
2. **退款与撤销**：提供便捷的退款服务，并支持在特定条件下撤销未完成的交易。
3. **报告与统计分析**：生成详细的财务报表及销售趋势分析，帮助企业更好地理解其经营状况并作出决策。

### 视觉分析模块

1. **实时面部识别与情绪分析**：通过摄像头捕捉学生的面部表情变化，利用AI技术分析学生的情绪状态（如专注、困惑、疲惫等），从而判断学生当前的学习状态。
2. **行为模式监测**：除了面部表情外，还可以监测学生的身体语言和其他行为模式（比如是否频繁查看手机、是否有做笔记的动作等），进一步了解学生在课堂上的参与度和注意力集中情况。
3. **异常行为检测**：设定一定的规则或标准来定义哪些行为被视为“异常”，例如长时间离开座位、闭眼休息时间过长等。当检测到此类行为时，将其记录下来并进行相应处理。
4. **数据记录与分析**：将每次课程中收集到的所有相关信息（包括但不限于情绪变化、行为模式、异常行为等）存储于数据库中，以便后续进行深入的数据挖掘和分析工作。
5. **个性化反馈与建议**：

- 对学生：根据其表现给出个性化的学习建议，比如调整学习方法、增加休息间隔等。
- 对教师：提供班级整体及个别学生的数据分析报告，帮助教师更好地理解学生需求，优化教学策略。

1. **自动预警机制**：一旦发现某位学生连续多次出现不良学习状态或特定类型的异常行为达到预设阈值时，系统能够自动向该生及其家长/监护人发送提醒信息；同时也会通知相关教师关注该学生的情况。
2. **隐私保护措施**：鉴于涉及个人信息的敏感性，在设计和实施上述功能时必须严格遵守相关法律法规，确保用户数据的安全性和私密性不受侵犯。例如，明确告知用户数据收集的目的、范围以及如何使用这些信息；允许用户随时撤回同意授权等。

## 四、核心业务流程

流程图： 



文字描述：

1. 用户注册 => 用户登录
2. 用户创建应用 => 创建题目（包括题目选项得分）=> 创建评分规则（评分策略和评分结果）
3. 管理员管理应用，审核发布（或下架）应用
4. 用户查看和检索应用列表，进入应用详情页，在线答题并提交回答
5. 经过评分模块计算后，用户可查看本次评分结果

## 五、项目Demo

### 主页

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711073344-bebfe335-dd28-4cd6-ba77-6d5206b92563.png)

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711149436-b544bf21-4bf3-499f-8aa1-0b666e618c99.png)

### **聊天页面**

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711215760-d40772bb-2c73-4e08-b543-89da15633bd9.png)

### 圈子页面

### ![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711722464-c975f0ff-5bc6-4c95-bd32-a994b4408087.png)

### 课程页面

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711793500-7388a4bc-5e1b-4c4f-a9f8-3256e7289a9c.png)

### 我的页面

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746711858447-cff29124-c547-4d07-8616-5fe50ca25a95.png)

### 管理页面

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746716536532-5334dac6-2d5c-42f8-8930-a70adf09e46f.png)

![img](https://cdn.nlark.com/yuque/0/2025/png/50745479/1746716596170-23b31f2a-969f-41b8-9fa4-71ef8cf7c1e6.png)

## 六、技术架构图

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1746947680196-8fae4725-a9c0-4bd8-b59c-2184d9d37e7a.jpeg)



## 七、时序图

### 用户模块

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1746714213391-0148c183-f709-42a0-bc1e-b771d74c7476.jpeg)

### 智慧体模块

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741669556875-f93b77de-7138-4d0b-bb6c-df202883f5df.jpeg)

### 圈子模块

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741669556875-f93b77de-7138-4d0b-bb6c-df202883f5df.jpeg)



## 八、接口文档

http://10.16.62.100:12345/api/doc.html#/home

## 九、数据库表设计

### 1. 用户管理

#### 用户注册与登录

- **功能描述**：用户可以通过账号密码注册并登录系统，同时支持微信开放平台和公众号的第三方登录方式。
- **流程**：

1. 用户输入账号（userAccount）和密码（userPassword）进行注册，或通过微信授权获取unionId和mpOpenId。
2. 系统验证信息后创建用户记录，分配唯一id，默认角色为student。
3. 登录时，系统校验账号密码或微信标识，验证通过后返回用户身份。

#### 用户信息维护

- **功能描述**：用户可以编辑个人资料，包括昵称、头像、简介、性别、手机号等信息。
- **流程**：

1. 用户登录后访问个人中心，编辑字段如userName、userAvatar、userProfile、userGender等。
2. 系统更新user表对应记录，记录更新时间（updateTime）。

#### 用户角色

- **功能描述**：系统支持多种角色，包括学生（student）、教师（teacher）、管理员（admin）和被禁用户（ban）。
- **流程**：

1. 用户注册时默认角色为student，管理员可手动调整userRole字段。
2. 不同角色拥有不同权限，如教师可创建班级，管理员可管理系统内容。

------

### 2. 社交功能

#### 帖子发布与管理

- **功能描述**：用户可以发布帖子，支持标题、内容、标签，并可查看点赞、收藏和评论数。
- **流程**：

1. 用户填写帖子信息（title、content、tags），提交后生成记录到post表。
2. 系统记录创建者userId、创建时间（createTime）和帖子类型（type）。
3. 其他用户可通过post_thumb表点赞（thumbNum增加），通过post_favour表收藏（favourNum增加）。

#### 帖子评论与回复

- **功能描述**：用户可以对帖子发表评论，并回复已有评论。
- **流程**：

1. 用户针对帖子（postId）提交评论（content），记录到post_comment表，commentNum增加。
2. 用户针对评论（commentId）提交回复，记录到post_comment_reply表。
3. 系统支持按postId或userId检索评论和回复。

#### 好友关系

- **功能描述**：用户可以发送好友申请，建立好友关系。
- **流程**：

1. 用户A向用户B发送申请，记录到friend_request表，状态为pending。
2. 用户B接受后，状态更新为accepted，并在friend_relationship表中建立双向关系。
3. 关系状态支持blocked，表示屏蔽好友。

#### 私聊功能

- **功能描述**：好友之间可以进行私聊，发送文本消息。
- **流程**：

1. 好友关系建立后，系统在private_chat_session表创建会话，记录userId1和userId2。
2. 用户发送消息，记录到private_message表，标记isRead状态。
3. 接收方查看消息后，isRead更新为1，记录最后消息时间（lastMessageTime）。

### 3. 课程管理

#### 课程创建与发布

- **功能描述**：管理员或教师可以创建课程，设置课程信息、章节和小节。
- **流程**：

1. 管理员在后台创建课程，填写title、description、price等信息。
2. 为课程添加章节（course_chapter）和小节（course_section），小节支持多种资源类型（视频、音频等）。
3. 设置课程状态为已发布后，用户可查看和购买课程。

#### 课程学习与进度

- **功能描述**：用户购买课程后可学习，系统记录学习进度和完成情况。
- **流程**：

1. 用户购买课程后，记录在user_course表。
2. 用户学习小节时，系统更新user_course_progress表中的progress和watchDuration。
3. 完成小节后，标记isCompleted为1，记录完成时间。

#### 课程评价

- **功能描述**：用户可对课程进行评价和打分。
- **流程**：

1. 用户提交评价内容和评分，记录到course_review表。
2. 管理员审核评价后，更新课程的ratingScore和ratingCount。

------

### 4. 学习记录与统计

#### 用户学习统计

- **功能描述**：系统记录用户的学习天数、打卡记录和经验值。
- **流程**：

1. 用户每日打卡，更新user_learning_stats中的learningDays和continuousCheckIn。
2. 学习活动（如完成课程、阅读文章）增加经验值（experience），提升等级（level）。

#### 学习目标管理

- **功能描述**：用户可设置每日学习目标，系统跟踪完成情况。
- **流程**：

1. 用户设置每日目标，记录在user_daily_goal和user_goal_item表。
2. 用户完成学习任务后，更新目标进度（currentValue），完成时标记isCompleted。

#### 学习记录

- **功能描述**：记录用户的具体学习活动，如学习单词、阅读文章。
- **流程**：

1. 用户学习单词或文章时，记录在user_word_record和user_article_record表。
2. 系统根据学习状态（如learningStatus）为用户推荐复习内容。

------

### 5. 每日学习内容

#### 每日单词

- **功能描述**：管理员发布每日单词，用户可学习并记录掌握情况。
- **流程**：

1. 管理员在daily_word表中添加单词，设置publishDate。
2. 用户学习单词后，更新user_word_record中的learningStatus。

#### 每日文章

- **功能描述**：管理员发布每日文章，用户可阅读并记录阅读进度。
- **流程**：

1. 管理员在daily_article表中添加文章，设置publishDate。
2. 用户阅读文章时，更新user_article_record中的readProgress和readStatus。

------

### 6. 成就系统

#### 成就定义与获取

- **功能描述**：系统预定义多种成就，用户通过完成任务获得成就。
- **流程**：

1. 管理员在achievement表中定义成就，设置conditionType和conditionValue。
2. 用户完成相应任务后，系统自动检测并更新user_achievement表中的progress。
3. 达到条件后，标记isCompleted为1，发放奖励。

#### 成就展示

- **功能描述**：用户可在个人资料中展示已获得的成就。
- **流程**：

1. 用户在user_achievement_display表中选择展示的成就。
2. 系统根据displayType在用户资料页展示成就图标或横幅。

#### 里程碑系统

- **功能描述**：用户通过累积成就点数解锁里程碑，获得额外奖励。
- **流程**：

1. 管理员在achievement_milestone表中设置里程碑，定义requiredPoints。
2. 用户的成就点数（currentPoints）达到要求后，解锁里程碑，记录在user_milestone表。

### 7. 系统管理

#### 公告发布与管理

- **功能描述**：管理员可以发布系统公告，用户可查看和标记已读。
- **流程**：

1. 管理员在后台创建公告，填写title、content、priority等信息。
2. 公告发布后，用户在登录时系统推送未读公告。
3. 用户查看公告后，系统记录在announcement_read表中。

#### 用户反馈处理

- **功能描述**：用户可以提交反馈，管理员处理并回复。
- **流程**：

1. 用户提交反馈，记录在user_feedback表，状态为待处理。
2. 管理员查看反馈，处理后更新status为已处理，并可回复用户。
3. 用户可在反馈页面查看处理结果和回复。

------

### 8. AI功能

#### AI聊天助手

- **功能描述**：用户可以与AI助手进行对话，获取学习建议或解答疑问。
- **流程**：

1. 用户创建聊天会话，记录在chat_session表。
2. 用户发送消息，系统记录在chat_message表，AI回复后再次记录。
3. 系统支持多轮对话，用户可查看历史记录。

#### AI分身管理

- **功能描述**：管理员可以创建和管理AI分身，用户可选择使用。
- **流程**：

1. 管理员在ai_avatar表中创建AI分身，设置name、description等。
2. 用户在user_ai_avatar表中选择并使用AI分身，记录使用次数。
3. 用户可对AI分身进行评分和反馈，更新rating和ratingCount。

------

## 十、数据库设计

### 核心表结构

#### 用户表（user）

- **作用**：存储用户基本信息和角色。
- **字段**：

- id：主键，自增。
- userAccount：账号，必填。
- userPassword：密码，必填。
- userRole：用户角色，默认student。
- unionId、mpOpenId：微信登录标识。
- userName、userAvatar、userProfile：用户资料。
- createTime、updateTime：时间戳。
- isDelete：软删除标记。

#### 帖子表（post）

- **作用**：存储用户发布的帖子。
- **字段**：

- id：主键，自增。
- title、content、tags：帖子内容。
- thumbNum、favourNum、commentNum：互动统计。
- userId：创建者ID，外键关联user表。
- type：帖子类型。

#### 帖子点赞表（post_thumb）

- **作用**：记录用户对帖子的点赞。
- **字段**：

- postId：帖子ID。
- userId：点赞用户ID。

#### 帖子收藏表（post_favour）

- **作用**：记录用户对帖子的收藏。
- **字段**：

- postId：帖子ID。
- userId：收藏用户ID。

#### 帖子评论表（post_comment）

- **作用**：存储帖子评论。
- **字段**：

- postId：帖子ID，外键。
- userId：评论者ID，外键。
- content：评论内容。

#### 帖子评论回复表（post_comment_reply）

- **作用**：存储评论的回复。
- **字段**：

- postId：帖子ID。
- commentId：评论ID，外键。
- userId：回复者ID。

#### 好友关系表（friend_relationship）

- **作用**：存储用户之间的好友关系。
- **字段**：

- userId1、userId2：好友双方ID。
- status：状态（pending/accepted/blocked）。

#### 好友申请表（friend_request）

- **作用**：存储好友申请记录。
- **字段**：

- senderId：发送者ID。
- receiverId：接收者ID。
- status：状态（pending/accepted/rejected）。

#### 私聊会话表（private_chat_session）

- **作用**：存储私聊会话。
- **字段**：

- userId1、userId2：会话双方ID。
- lastMessageTime：最后消息时间。

#### 私聊消息表（private_message）

- **作用**：存储私聊消息。
- **字段**：

- senderId、receiverId：发送和接收者ID。
- content：消息内容。
- isRead：是否已读。

#### 课程表（course）

- **作用**：存储课程基本信息。
- **字段**：

- id：主键。
- title、description、price：课程信息。
- courseType：课程类型（公开、付费、会员）。
- status：发布状态。

#### 课程章节表（course_chapter）

- **作用**：存储课程章节。
- **字段**：

- courseId：关联课程。
- title：章节标题。
- sort：章节排序。

#### 课程小节表（course_section）

- **作用**：存储课程小节。
- **字段**：

- courseId、chapterId：关联课程和章节。
- title、videoUrl：小节内容。
- isFree：是否免费。

#### 用户课程购买记录表（user_course）

- **作用**：记录用户购买的课程。
- **字段**：

- userId、courseId：用户和课程ID。
- status：购买状态。

#### 用户学习进度表（user_course_progress）

- **作用**：记录用户在课程中的学习进度。
- **字段**：

- userId、courseId、sectionId：关联用户、课程和小节。
- progress：学习进度百分比。
- isCompleted：是否完成。

#### 用户学习统计表（user_learning_stats）

- **作用**：记录用户的学习统计数据。
- **字段**：

- userId：用户ID。
- level、experience：等级和经验值。
- learningDays、continuousCheckIn：学习天数和连续打卡。

#### 学习目标类型表（goal_type）

- **作用**：定义学习目标的类型。
- **字段**：

- name、code：目标名称和编码。
- points、experience：完成奖励。

#### 用户每日学习目标表（user_daily_goal）

- **作用**：记录用户每日的学习目标。
- **字段**：

- userId、goalDate：用户和目标日期。
- totalGoals、completedGoals：目标总数和完成数。

#### 每日单词表（daily_word）

- **作用**：存储每日发布的单词。
- **字段**：

- word、translation：单词和翻译。
- publishDate：发布日期。

#### 每日文章表（daily_article）

- **作用**：存储每日发布的文章。
- **字段**：

- title、content：文章标题和内容。
- publishDate：发布日期。

#### 成就表（achievement）

- **作用**：定义系统中的成就。
- **字段**：

- name、description：成就名称和描述。
- conditionType、conditionValue：成就条件。

#### 用户成就表（user_achievement）

- **作用**：记录用户获得的成就。
- **字段**：

- userId、achievementId：用户和成就ID。
- progress、isCompleted：进度和完成状态。

#### 公告表（announcement）

- **作用**：存储系统公告。
- **字段**：

- id：主键。
- title、content：公告标题和内容。
- status：公告状态（草稿、已发布、已下线）。

#### 公告阅读记录表（announcement_read）

- **作用**：记录用户阅读公告的情况。
- **字段**：

- announcementId、userId：公告和用户ID。
- createTime：阅读时间。

#### 用户反馈表（user_feedback）

- **作用**：存储用户反馈。
- **字段**：

- userId：反馈用户ID。
- feedbackType、content：反馈类型和内容。
- status：处理状态。

#### 反馈回复表（user_feedback_reply）

- **作用**：记录管理员对用户反馈的回复。
- **字段**：

- feedbackId：关联的反馈ID。
- senderId、senderRole：发送者ID和角色。
- content：回复内容。

#### 聊天会话表（chat_session）

- **作用**：存储用户的AI聊天会话。
- **字段**：

- userId：用户ID。
- sessionName：会话名称。
- aiModel：使用的AI模型。

#### 聊天消息表（chat_message）

- **作用**：存储聊天消息。
- **字段**：

- sessionId：会话ID。
- userId：用户ID。
- content、role：消息内容和角色（user/assistant）。

#### AI分身表（ai_avatar）

- **作用**：存储AI分身信息。
- **字段**：

- name、description：分身名称和描述。
- isPublic：是否公开。
- usageCount：使用次数。

#### 用户AI分身关联表（user_ai_avatar）

- **作用**：记录用户与AI分身的关系。
- **字段**：

- userId、aiAvatarId：用户和AI分身ID。
- isFavorite：是否收藏。
- useCount：使用次数。

------

```sql
# 数据库初始化
-- 创建库
create database if not exists smart_class;

-- 切换库
use smart_class;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userGender   int                                    null comment '性别 0-男 1-女 2-保密',
    userPhone    varchar(256)                           null comment '手机号',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'student'         not null comment '用户角色：student/teacher/admin/ban',
    province     varchar(100)                           null comment '省份',
    city         varchar(100)                           null comment '城市',
    district     varchar(100)                           null comment '区县',
    wechatId     varchar(256)                           null comment '微信号',
    userEmail        varchar(256)                           null comment '邮箱',
    birthday     datetime                               null comment '生日',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
CREATE TABLE IF NOT EXISTS post
(
    id         BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    title      VARCHAR(512)                       NULL COMMENT '标题',
    content    TEXT                               NULL COMMENT '内容',
    tags       VARCHAR(1024)                      NULL COMMENT '标签列表（json 数组）',
    thumbNum   INT      DEFAULT 0                 NOT NULL COMMENT '点赞数',
    favourNum  INT      DEFAULT 0                 NOT NULL COMMENT '收藏数',
    commentNum INT      DEFAULT 0                 NOT NULL COMMENT '评论数',
    userId     BIGINT                             NOT NULL COMMENT '创建用户 id',
    country    VARCHAR(100)                       NULL COMMENT '国家',
    city       VARCHAR(100)                       NULL COMMENT '城市',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    type       VARCHAR(50)                        NOT NULL COMMENT '帖子类型，如学习/生活/技巧',
    INDEX idx_userId (userId),
    CONSTRAINT fk_post_user FOREIGN KEY (userId) REFERENCES user(id)
) COMMENT '帖子' COLLATE = utf8mb4_unicode_ci;

-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 帖子评论表
CREATE TABLE IF NOT EXISTS post_comment
(
    id         BIGINT AUTO_INCREMENT COMMENT '评论ID' PRIMARY KEY,
    postId     BIGINT                             NOT NULL COMMENT '帖子ID，关联到post表',
    userId     BIGINT                             NOT NULL COMMENT '评论者ID，关联到user表',
    content    TEXT                               NOT NULL COMMENT '评论内容',
    country    VARCHAR(100)                       NULL COMMENT '国家',
    city       VARCHAR(100)                       NULL COMMENT '城市',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_postId (postId),
    INDEX idx_userId (userId),
    CONSTRAINT fk_comment_post FOREIGN KEY (postId) REFERENCES post(id),
    CONSTRAINT fk_comment_user FOREIGN KEY (userId) REFERENCES user(id)
) COMMENT '帖子评论' COLLATE = utf8mb4_unicode_ci;

-- 帖子评论回复表
CREATE TABLE IF NOT EXISTS post_comment_reply
(
    id         BIGINT AUTO_INCREMENT COMMENT '回复ID' PRIMARY KEY,
    postId     BIGINT                             NOT NULL COMMENT '帖子ID，关联到post表',
    commentId  BIGINT                             NOT NULL COMMENT '评论ID，关联到post_comment表',
    userId     BIGINT                             NOT NULL COMMENT '回复者ID，关联到user表',
    content    TEXT                               NOT NULL COMMENT '回复内容',
    country    VARCHAR(100)                       NULL COMMENT '国家',
    city       VARCHAR(100)                       NULL COMMENT '城市',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_postId (postId),
    INDEX idx_commentId (commentId),
    INDEX idx_userId (userId),
    CONSTRAINT fk_reply_post FOREIGN KEY (postId) REFERENCES post (id),
    CONSTRAINT fk_reply_comment FOREIGN KEY (commentId) REFERENCES post_comment (id),
    CONSTRAINT fk_reply_user FOREIGN KEY (userId) REFERENCES user (id)
) COMMENT '帖子评论回复' COLLATE = utf8mb4_unicode_ci;

-- 用户等级表
create table if not exists user_level
(
    id            bigint auto_increment comment 'id' primary key,
    level         int                                not null comment '等级数值',
    levelName     varchar(64)                        not null comment '等级名称',
    iconUrl       varchar(1024)                      null comment '等级图标URL',
    minExperience int                                not null comment '最小经验值',
    maxExperience int                                not null comment '最大经验值',
    description   varchar(512)                       null comment '等级描述',
    privileges    varchar(1024)                      null comment '等级特权，JSON格式',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_level (level)
) comment '用户等级' collate = utf8mb4_unicode_ci;

-- 用户学习统计表
create table if not exists user_learning_stats
(
    id                bigint auto_increment comment 'id' primary key,
    userId            bigint                             not null comment '用户id',
    level             int      default 1                 not null comment '当前等级',
    experience        int      default 0                 not null comment '当前经验值',
    nextLevelExp      int      default 100               not null comment '下一级所需经验值',
    learningDays      int      default 0                 not null comment '学习天数',
    continuousCheckIn int      default 0                 not null comment '连续打卡天数',
    totalCheckIn      int      default 0                 not null comment '总打卡天数',
    totalPoints       int      default 0                 not null comment '总积分',
    totalBadges       int      default 0                 not null comment '获得徽章数',
    lastCheckInTime   datetime                           null comment '最后打卡时间',
    createTime        datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_level (level),
    index idx_experience (experience),
    index idx_learningDays (learningDays),
    index idx_continuousCheckIn (continuousCheckIn)
) comment '用户学习统计' collate = utf8mb4_unicode_ci;

-- 用户与每日文章关联表
CREATE TABLE IF NOT EXISTS user_daily_article
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    articleId      BIGINT                             NOT NULL COMMENT '文章id',
    isRead         TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否阅读：0-否，1-是',
    readTime       DATETIME                           NULL COMMENT '阅读时间',
    isLiked        TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否点赞：0-否，1-是',
    likeTime       DATETIME                           NULL COMMENT '点赞时间',
    isCollected    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否收藏：0-否，1-是',
    collectTime    DATETIME                           NULL COMMENT '收藏时间',
    commentContent TEXT                               NULL COMMENT '评论内容',
    commentTime    DATETIME                           NULL COMMENT '评论时间',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_articleId (articleId),
    UNIQUE uk_user_article (userId, articleId)
) COMMENT '用户与每日文章关联' COLLATE = utf8mb4_unicode_ci;

-- 用户与每日单词关联表
CREATE TABLE IF NOT EXISTS user_daily_word
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    wordId         BIGINT                             NOT NULL COMMENT '单词id',
    isStudied      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否学习：0-否，1-是',
    studyTime      DATETIME                           NULL COMMENT '学习时间',
    isLiked        TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否点赞：0-否，1-是',
    likeTime       DATETIME                           NULL COMMENT '点赞时间',
    isCollected    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否收藏：0-否，1-是',
    collectTime    DATETIME                           NULL COMMENT '收藏时间',
    noteContent    TEXT                               NULL COMMENT '笔记内容',
    noteTime       DATETIME                           NULL COMMENT '笔记时间',
    masteryLevel   TINYINT  DEFAULT 0                 NOT NULL COMMENT '掌握程度：0-未知，1-生词，2-熟悉，3-掌握',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_wordId (wordId),
    UNIQUE uk_user_word (userId, wordId)
) COMMENT '用户与每日单词关联' COLLATE = utf8mb4_unicode_ci;
-- 用户与公告关联表
CREATE TABLE IF NOT EXISTS user_announcement_reader
(
    id              BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id',
    announcementId BIGINT                             NOT NULL COMMENT '公告id',
    readTime       DATETIME                           NULL COMMENT '阅读时间',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (userId),
    INDEX idx_announcement_id (announcementId),
    UNIQUE uk_user_announcement (userId, announcementId)
) COMMENT '用户公告阅读记录' COLLATE = utf8mb4_unicode_ci;

-- 用户每日学习目标表
create table if not exists user_daily_goal
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    goalDate        date                               not null comment '目标日期',
    totalGoals      int      default 0                 not null comment '总目标数',
    completedGoals  int      default 0                 not null comment '已完成目标数',
    progressPercent int      default 0                 not null comment '完成百分比',
    isCompleted     tinyint  default 0                 not null comment '是否全部完成：0-否，1-是',
    completedTime   datetime                           null comment '全部完成时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_goalDate (goalDate),
    unique uk_user_date (userId, goalDate)
) comment '用户每日学习目标' collate = utf8mb4_unicode_ci;

-- 学习目标类型表
create table if not exists goal_type
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128)                       not null comment '目标类型名称',
    code         varchar(64)                        not null comment '目标类型编码',
    icon         varchar(1024)                      null comment '图标URL',
    description  varchar(512)                       null comment '描述',
    category     varchar(64)                        not null comment '分类',
    unit         varchar(32)                        null comment '单位',
    defaultValue int      default 1                 not null comment '默认值',
    minValue     int      default 1                 not null comment '最小值',
    `maxValue`   int                                null comment '最大值',
    points       int      default 5                 not null comment '完成可获得积分',
    experience   int      default 10                not null comment '完成可获得经验值',
    isSystem     tinyint  default 1                 not null comment '是否系统预设：0-否，1-是',
    isEnabled    tinyint  default 1                 not null comment '是否启用：0-否，1-是',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_code (code),
    index idx_category (category)
) comment '学习目标类型' collate = utf8mb4_unicode_ci;

-- 用户学习目标项表
create table if not exists user_goal_item
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    dailyGoalId     bigint                             not null comment '每日目标id',
    goalTypeId      bigint                             not null comment '目标类型id',
    goalDate        date                               not null comment '目标日期',
    title           varchar(256)                       not null comment '目标标题',
    targetValue     int      default 1                 not null comment '目标值',
    currentValue    int      default 0                 not null comment '当前值',
    progressPercent int      default 0                 not null comment '完成百分比',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    isRewarded      tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime      datetime                           null comment '奖励发放时间',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_dailyGoalId (dailyGoalId),
    index idx_goalTypeId (goalTypeId),
    index idx_goalDate (goalDate),
    index idx_isCompleted (isCompleted)
) comment '用户学习目标项' collate = utf8mb4_unicode_ci;

-- 用户学习记录表
CREATE TABLE IF NOT EXISTS user_learning_record
(
    id           BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId       BIGINT                             NOT NULL COMMENT '用户id',
    recordDate   DATE                               NOT NULL COMMENT '记录日期',
    recordType   VARCHAR(64)                        NOT NULL COMMENT '记录类型，如：word_card, listening, course等',
    relatedId    BIGINT                             NULL COMMENT '关联ID，如单词ID、课程ID等',
    lessonNumber INT      DEFAULT 0                 NOT NULL COMMENT '课程中的课次或子活动编号',
    duration     INT      DEFAULT 0                 NOT NULL COMMENT '学习时长(秒)',
    count        INT      DEFAULT 1                 NOT NULL COMMENT '学习数量',
    points       INT      DEFAULT 0                 NOT NULL COMMENT '获得积分',
    experience   INT      DEFAULT 0                 NOT NULL COMMENT '获得经验值',
    accuracy     DECIMAL(5,2) DEFAULT 0            NOT NULL COMMENT '正确率(百分比)',
    status       VARCHAR(32) DEFAULT 'completed'   NOT NULL COMMENT '活动状态，如：in_progress, completed, failed',
    remark       VARCHAR(512)                       NULL COMMENT '备注',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_recordDate (recordDate),
    INDEX idx_recordType (recordType),
    UNIQUE INDEX idx_unique_record (userId, recordDate, recordType, relatedId)

) COMMENT '用户学习记录' COLLATE = utf8mb4_unicode_ci;


-- 成就定义表
create table if not exists achievement
(
    id                   bigint auto_increment comment 'id' primary key,
    name                 varchar(128)                       not null comment '成就名称',
    description          varchar(512)                       not null comment '成就描述',
    iconUrl              varchar(1024)                      not null comment '成就图标URL',
    badgeUrl             varchar(1024)                      not null comment '成就徽章URL',
    bannerUrl            varchar(1024)                      null comment '成就横幅URL',
    category             varchar(64)                        not null comment '成就分类，如：学习、社交、活动等',
    level                tinyint  default 1                 not null comment '成就等级：1-普通，2-稀有，3-史诗，4-传说',
    points               int      default 0                 not null comment '成就点数',
    achievementCondition varchar(512)                       not null comment '获取条件描述',
    conditionType        varchar(64)                        not null comment '条件类型，如：course_complete, login_days, article_read等',
    conditionValue       int      default 1                 not null comment '条件值，如完成10门课程，登录30天等',
    isHidden             tinyint  default 0                 not null comment '是否隐藏成就：0-否，1-是，隐藏成就不会提前显示给用户',
    isSecret             tinyint  default 0                 not null comment '是否是彩蛋成就：0-否，1-是，彩蛋成就是特殊发现的成就',
    rewardType           varchar(64)                        null comment '奖励类型，如：points, badge, coupon等',
    rewardValue          varchar(256)                       null comment '奖励值，如积分数量、优惠券ID等',
    sort                 int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId              bigint                             not null comment '创建管理员id',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_level (level),
    index idx_conditionType (conditionType),
    index idx_sort (sort)
) comment '成就定义' collate = utf8mb4_unicode_ci;

-- 用户成就表
create table if not exists user_achievement
(
    id              bigint auto_increment comment 'id' primary key,
    userId          bigint                             not null comment '用户id',
    achievementId   bigint                             not null comment '成就id',
    progress        int      default 0                 not null comment '当前进度值',
    progressMax     int      default 1                 not null comment '目标进度值',
    progressPercent int      default 0                 not null comment '进度百分比',
    isCompleted     tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime   datetime                           null comment '完成时间',
    isRewarded      tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime      datetime                           null comment '奖励发放时间',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_achievementId (achievementId),
    index idx_isCompleted (isCompleted),
    unique uk_user_achievement (userId, achievementId)
) comment '用户成就' collate = utf8mb4_unicode_ci;

-- 成就展示配置表
create table if not exists achievement_display
(
    id              bigint auto_increment comment 'id' primary key,
    achievementId   bigint                             not null comment '成就id',
    displayType     varchar(64)                        not null comment '展示类型：profile(个人资料页), card(成就卡片), banner(成就横幅), popup(弹窗通知)',
    title           varchar(256)                       null comment '展示标题，为空则使用成就名称',
    subtitle        varchar(512)                       null comment '展示副标题',
    imageUrl        varchar(1024)                      null comment '展示图片URL，为空则使用成就图标',
    backgroundColor varchar(32)                        null comment '背景颜色，十六进制颜色代码',
    textColor       varchar(32)                        null comment '文字颜色，十六进制颜色代码',
    animationType   varchar(64)                        null comment '动画类型',
    displayDuration int      default 0                 not null comment '展示时长(秒)，0表示永久展示',
    sort            int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId         bigint                             not null comment '创建管理员id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_achievementId (achievementId),
    index idx_displayType (displayType),
    index idx_sort (sort)
) comment '成就展示配置' collate = utf8mb4_unicode_ci;

-- 用户成就展示记录表
create table if not exists user_achievement_display
(
    id                   bigint auto_increment comment 'id' primary key,
    userId               bigint                             not null comment '用户id',
    achievementId        bigint                             not null comment '成就id',
    achievementDisplayId bigint                             not null comment '成就展示配置id',
    isEnabled            tinyint  default 1                 not null comment '是否启用展示：0-否，1-是',
    isPinned             tinyint  default 0                 not null comment '是否置顶：0-否，1-是',
    customTitle          varchar(256)                       null comment '自定义标题，为空则使用默认标题',
    customImageUrl       varchar(1024)                      null comment '自定义图片URL，为空则使用默认图片',
    displayCount         int      default 0                 not null comment '展示次数',
    lastDisplayTime      datetime                           null comment '最后展示时间',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_achievementId (achievementId),
    index idx_achievementDisplayId (achievementDisplayId),
    unique uk_user_achievement_display (userId, achievementId, achievementDisplayId)
) comment '用户成就展示记录' collate = utf8mb4_unicode_ci;

-- 成就里程碑表
create table if not exists achievement_milestone
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(128)                       not null comment '里程碑名称',
    description    varchar(512)                       not null comment '里程碑描述',
    iconUrl        varchar(1024)                      not null comment '里程碑图标URL',
    bannerUrl      varchar(1024)                      null comment '里程碑横幅URL',
    category       varchar(64)                        not null comment '里程碑分类',
    requiredPoints int      default 0                 not null comment '所需成就点数',
    rewardType     varchar(64)                        null comment '奖励类型',
    rewardValue    varchar(256)                       null comment '奖励值',
    sort           int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId        bigint                             not null comment '创建管理员id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    index idx_category (category),
    index idx_requiredPoints (requiredPoints),
    index idx_sort (sort)
) comment '成就里程碑' collate = utf8mb4_unicode_ci;

-- 用户里程碑表
create table if not exists user_milestone
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    milestoneId   bigint                             not null comment '里程碑id',
    currentPoints int      default 0                 not null comment '当前成就点数',
    isCompleted   tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime datetime                           null comment '完成时间',
    isRewarded    tinyint  default 0                 not null comment '是否已发放奖励：0-否，1-是',
    rewardTime    datetime                           null comment '奖励发放时间',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_milestoneId (milestoneId),
    index idx_isCompleted (isCompleted),
    unique uk_user_milestone (userId, milestoneId)
) comment '用户里程碑' collate = utf8mb4_unicode_ci;

-- 公告表
create table if not exists announcement
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(256)                       not null comment '公告标题',
    content    text                               not null comment '公告内容',
    priority   int      default 0                 not null comment '优先级，数字越大优先级越高',
    status     tinyint  default 1                 not null comment '状态：0-草稿，1-已发布，2-已下线',
    startTime  datetime                           null comment '公告开始展示时间',
    endTime    datetime                           null comment '公告结束展示时间',
    coverImage varchar(1024)                      null comment '封面图片URL',
    adminId    bigint                             not null comment '发布管理员id',
    viewCount  int      default 0                 not null comment '查看次数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_adminId (adminId),
    index idx_status (status),
    index idx_priority (priority)
) comment '系统公告' collate = utf8mb4_unicode_ci;

-- 公告阅读记录表
create table if not exists announcement_read
(
    id             bigint auto_increment comment 'id' primary key,
    announcementId bigint                             not null comment '公告id',
    userId         bigint                             not null comment '用户id',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '阅读时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_announcementId (announcementId),
    index idx_userId (userId),
    unique uk_announcement_user (announcementId, userId)
) comment '公告阅读记录' collate = utf8mb4_unicode_ci;


-- 课程分类表
create table if not exists course_category
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(128)                       not null comment '分类名称',
    description varchar(512)                       null comment '分类描述',
    icon        varchar(1024)                      null comment '分类图标URL',
    sort        int      default 0                 not null comment '排序权重，数字越大排序越靠前',
    parentId    bigint   default 0                 not null comment '父分类id，0表示一级分类',
    adminId     bigint                             not null comment '创建管理员id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_parentId (parentId),
    index idx_sort (sort)
) comment '课程分类' collate = utf8mb4_unicode_ci;

-- 课程表
create table if not exists course
(
    id             bigint auto_increment comment 'id' primary key,
    title          varchar(256)                             not null comment '课程标题',
    subtitle       varchar(512)                             null comment '课程副标题',
    description    text                                     null comment '课程描述',
    coverImage     varchar(1024)                            null comment '封面图片URL',
    price          decimal(10, 2) default 0.00              not null comment '课程价格',
    originalPrice  decimal(10, 2) default 0.00              null comment '原价',
    courseType     tinyint        default 0                 not null comment '课程类型：0-公开课，1-付费课，2-会员课',
    difficulty     tinyint        default 1                 not null comment '难度等级：1-入门，2-初级，3-中级，4-高级，5-专家',
    status         tinyint        default 0                 not null comment '状态：0-未发布，1-已发布，2-已下架',
    categoryId     bigint                                   not null comment '课程分类id',
    teacherId      bigint                                   not null comment '讲师id',
    totalDuration  int            default 0                 not null comment '总时长(分钟)',
    totalChapters  int            default 0                 not null comment '总章节数',
    totalSections  int            default 0                 not null comment '总小节数',
    studentCount   int            default 0                 not null comment '学习人数',
    ratingScore    decimal(2, 1)  default 0.0               not null comment '评分，1-5分',
    ratingCount    int            default 0                 not null comment '评分人数',
    tags           varchar(512)                             null comment '标签，JSON数组格式',
    requirements   text                                     null comment '学习要求',
    objectives     text                                     null comment '学习目标',
    targetAudience text                                     null comment '目标受众',
    adminId        bigint                                   not null comment '创建管理员id',
    createTime     datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint        default 0                 not null comment '是否删除',
    index idx_categoryId (categoryId),
    index idx_teacherId (teacherId),
    index idx_courseType (courseType),
    index idx_status (status),
    index idx_difficulty (difficulty)
) comment '课程' collate = utf8mb4_unicode_ci;

-- 讲师表
create table if not exists teacher
(
    id           bigint auto_increment comment 'id' primary key,
    name         varchar(128)                       not null comment '讲师姓名',
    avatar       varchar(1024)                      null comment '讲师头像URL',
    title        varchar(128)                       null comment '讲师职称',
    introduction text                               null comment '讲师简介',
    expertise    varchar(512)                       null comment '专业领域，JSON数组格式',
    userId       bigint                             null comment '关联的用户id，如果讲师也是平台用户',
    adminId      bigint                             not null comment '创建管理员id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '讲师' collate = utf8mb4_unicode_ci;

-- 课程章节表
create table if not exists course_chapter
(
    id          bigint auto_increment comment 'id' primary key,
    courseId    bigint                             not null comment '课程id',
    title       varchar(256)                       not null comment '章节标题',
    description text                               null comment '章节描述',
    sort        int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId     bigint                             not null comment '创建管理员id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程章节' collate = utf8mb4_unicode_ci;

-- 课程小节表
create table if not exists course_section
(
    id           bigint auto_increment comment 'id' primary key,
    courseId     bigint                             not null comment '课程id',
    chapterId    bigint                             not null comment '章节id',
    title        varchar(256)                       not null comment '小节标题',
    description  text                               null comment '小节描述',
    videoUrl     varchar(1024)                      null comment '视频URL',
    duration     int      default 0                 not null comment '时长(秒)',
    sort         int      default 0                 not null comment '排序，数字越小排序越靠前',
    isFree       tinyint  default 0                 not null comment '是否免费：0-否，1-是',
    resourceType tinyint  default 0                 not null comment '资源类型：0-视频，1-音频，2-文档，3-图片，4-直播',
    resourceUrl  varchar(1024)                      null comment '资源URL',
    adminId      bigint                             not null comment '创建管理员id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_chapterId (chapterId),
    index idx_sort (sort)
) comment '课程小节' collate = utf8mb4_unicode_ci;

-- 课程资料表
create table if not exists course_material
(
    id            bigint auto_increment comment 'id' primary key,
    courseId      bigint                             not null comment '课程id',
    title         varchar(256)                       not null comment '资料标题',
    description   text                               null comment '资料描述',
    fileUrl       varchar(1024)                      not null comment '文件URL',
    fileSize      bigint   default 0                 not null comment '文件大小(字节)',
    fileType      varchar(64)                        null comment '文件类型',
    downloadCount int      default 0                 not null comment '下载次数',
    sort          int      default 0                 not null comment '排序，数字越小排序越靠前',
    adminId       bigint                             not null comment '创建管理员id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_courseId (courseId),
    index idx_sort (sort)
) comment '课程资料' collate = utf8mb4_unicode_ci;

-- 用户课程购买记录表
create table if not exists user_course
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    courseId      bigint                             not null comment '课程id',
    orderNo       varchar(64)                        null comment '订单编号',
    price         decimal(10, 2)                     not null comment '购买价格',
    paymentMethod varchar(32)                        null comment '支付方式',
    paymentTime   datetime                           null comment '支付时间',
    expireTime    datetime                           null comment '过期时间，null表示永久有效',
    status        tinyint  default 1                 not null comment '状态：0-未支付，1-已支付，2-已过期，3-已退款',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_orderNo (orderNo),
    unique uk_user_course (userId, courseId)
) comment '用户课程购买记录' collate = utf8mb4_unicode_ci;

-- 用户学习进度表
create table if not exists user_course_progress
(
    id            bigint auto_increment comment 'id' primary key,
    userId        bigint                             not null comment '用户id',
    courseId      bigint                             not null comment '课程id',
    sectionId     bigint                             not null comment '小节id',
    progress      int      default 0                 not null comment '学习进度(百分比)',
    watchDuration int      default 0                 not null comment '观看时长(秒)',
    lastPosition  int      default 0                 not null comment '上次观看位置(秒)',
    isCompleted   tinyint  default 0                 not null comment '是否完成：0-否，1-是',
    completedTime datetime                           null comment '完成时间',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_sectionId (sectionId),
    unique uk_user_section (userId, sectionId)
) comment '用户学习进度' collate = utf8mb4_unicode_ci;

-- 课程评价表
create table if not exists course_review
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    courseId       bigint                             not null comment '课程id',
    content        text                               not null comment '评价内容',
    rating         tinyint  default 5                 not null comment '评分(1-5分)',
    likeCount      int      default 0                 not null comment '点赞数',
    replyCount     int      default 0                 not null comment '回复数',
    adminReply     text                               null comment '管理员回复',
    adminReplyTime datetime                           null comment '管理员回复时间',
    status         tinyint  default 1                 not null comment '状态：0-待审核，1-已发布，2-已拒绝',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_courseId (courseId),
    index idx_rating (rating)
) comment '课程评价' collate = utf8mb4_unicode_ci;

-- 课程收藏表
create table if not exists course_favourite
(
    id         bigint auto_increment comment 'id' primary key,
    userId     bigint                             not null comment '用户id',
    courseId   bigint                             not null comment '课程id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_courseId (courseId),
    unique uk_user_course (userId, courseId)
) comment '课程收藏' collate = utf8mb4_unicode_ci;

-- 聊天会话表
create table if not exists chat_session
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户id',
    sessionName varchar(256)                       null comment '会话名称',
    aiModel     varchar(128)                       not null comment 'AI模型类型',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment 'AI聊天会话' collate = utf8mb4_unicode_ci;

-- 聊天消息表
create table if not exists chat_message
(
    id         bigint auto_increment comment 'id' primary key,
    sessionId  bigint                             not null comment '会话id',
    userId     bigint                             not null comment '用户id',
    content    text                               not null comment '消息内容',
    role       varchar(32)                        not null comment '消息角色：user/assistant',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_sessionId (sessionId),
    index idx_userId (userId)
) comment 'AI聊天消息' collate = utf8mb4_unicode_ci;

-- 每日单词表
CREATE TABLE IF NOT EXISTS daily_word
(
    id                 BIGINT AUTO_INCREMENT COMMENT 'ID' PRIMARY KEY,
    word               VARCHAR(128) NOT NULL COMMENT '单词',
    pronunciation      VARCHAR(128) NULL COMMENT '音标',
    audioUrl           VARCHAR(2048) NULL COMMENT '发音音频URL',
    translation        VARCHAR(512) NOT NULL COMMENT '翻译',
    example            TEXT NULL COMMENT '例句',
    exampleTranslation TEXT NULL COMMENT '例句翻译',
    difficulty         TINYINT DEFAULT 1 NOT NULL COMMENT '难度等级：1-简单，2-中等，3-困难',
    category           VARCHAR(64) NULL COMMENT '单词分类（如名词、动词、商务英语等）',
    notes              TEXT NULL COMMENT '单词笔记或补充说明',
    likeCount          INT DEFAULT 0 NOT NULL COMMENT '点赞次数',
    publishDate        DATE NOT NULL COMMENT '发布日期',
    adminId            BIGINT NOT NULL COMMENT '创建管理员ID',
    createTime         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete           TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除：0-未删除，1-已删除',
    INDEX idx_publishDate (publishDate),
    INDEX idx_word (word),
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty)
) COMMENT '每日单词' COLLATE = utf8mb4_unicode_ci;
-- 每日文章表
create table if not exists daily_article
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(256)                       not null comment '文章标题',
    content     text                               not null comment '文章内容',
    summary     varchar(512)                       null comment '文章摘要',
    coverImage  varchar(1024)                      null comment '封面图片URL',
    author      varchar(128)                       null comment '作者',
    source      varchar(256)                       null comment '来源',
    sourceUrl   varchar(1024)                      null comment '原文链接',
    category    varchar(64)                        null comment '文章分类',
    tags        varchar(512)                       null comment '标签，JSON数组格式',
    difficulty  tinyint  default 1                 not null comment '难度等级：1-简单，2-中等，3-困难',
    readTime    int      default 0                 not null comment '预计阅读时间(分钟)',
    publishDate date                               not null comment '发布日期',
    adminId     bigint                             not null comment '创建管理员id',
    viewCount   int      default 0                 not null comment '查看次数',
    likeCount   int      default 0                 not null comment '点赞次数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_publishDate (publishDate),
    index idx_category (category),
    index idx_difficulty (difficulty)
) comment '每日文章' collate = utf8mb4_unicode_ci;

-- 用户单词学习记录表
create table if not exists user_word_record
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    wordId         bigint                             not null comment '单词id',
    learningStatus tinyint  default 0                 not null comment '学习状态：0-未学习，1-已学习，2-已掌握',
    reviewCount    int      default 0                 not null comment '复习次数',
    lastReviewTime datetime                           null comment '最后一次复习时间',
    userNotes      text                               null comment '用户笔记',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_wordId (wordId),
    unique uk_user_word (userId, wordId)
) comment '用户单词学习记录' collate = utf8mb4_unicode_ci;

-- 用户文章阅读记录表
create table if not exists user_article_record
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null comment '用户id',
    articleId    bigint                             not null comment '文章id',
    readStatus   tinyint  default 0                 not null comment '阅读状态：0-未读，1-阅读中，2-已读完',
    readProgress int      default 0                 not null comment '阅读进度(百分比)',
    isLiked      tinyint  default 0                 not null comment '是否点赞',
    userNotes    text                               null comment '用户笔记',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_articleId (articleId),
    unique uk_user_article (userId, articleId)
) comment '用户文章阅读记录' collate = utf8mb4_unicode_ci;

-- AI分身表
create table if not exists ai_avatar
(
    id             bigint auto_increment comment 'id' primary key,
    name           varchar(128)                            not null comment 'AI分身名称',
    baseUrl        varchar(1024)                           null comment '请求地址',
    description    text                                    null comment 'AI分身描述',
    avatarImgUrl   varchar(1024)                           null comment 'AI分身头像URL',
    avatarAuth     varchar(512)                            null comment 'AI分身鉴权，一串随机字符',
    tags           varchar(512)                            null comment '标签，JSON数组格式',
    personality    text                                    null comment '性格特点描述',
    abilities      text                                    null comment '能力描述',
    isPublic       tinyint       default 1                 not null comment '是否公开：0-否，1-是',
    status         tinyint       default 1                 not null comment '状态：0-禁用，1-启用',
    usageCount     int           default 0                 not null comment '使用次数',
    rating         decimal(2, 1) default 0.0               not null comment '评分，1-5分',
    ratingCount    int           default 0                 not null comment '评分人数',
    creatorId      bigint                                  not null comment '创建者id',
    sort           int           default 0                 not null comment '排序，数字越小排序越靠前',
    createTime     datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint       default 0                 not null comment '是否删除',
    index idx_tags (tags),
    index idx_creatorId (creatorId),
    index idx_status (status),
    index idx_sort (sort),
    index idx_usageCount (usageCount)
) comment 'AI分身' collate = utf8mb4_unicode_ci;

-- 用户AI分身关联表
create table if not exists user_ai_avatar
(
    id             bigint auto_increment comment 'id' primary key,
    userId         bigint                             not null comment '用户id',
    aiAvatarId     bigint                             not null comment 'AI分身id',
    isFavorite     tinyint  default 0                 not null comment '是否收藏：0-否，1-是',
    lastUseTime    datetime                           null comment '最后使用时间',
    useCount       int      default 0                 not null comment '使用次数',
    userRating     decimal(2, 1)                      null comment '用户评分，1-5分',
    userFeedback   text                               null comment '用户反馈',
    customSettings text                               null comment '用户自定义设置，JSON格式',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_isFavorite (isFavorite),
    unique uk_user_avatar (userId, aiAvatarId)
) comment '用户AI分身关联' collate = utf8mb4_unicode_ci;

-- AI分身对话历史表
create table if not exists ai_avatar_chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户id',
    aiAvatarId  bigint                             not null comment 'AI分身id',
    sessionId   varchar(64)                        not null comment '会话ID',
    sessionName varchar(512)                       null comment '会话总结标题',
    messageType varchar(32)                        not null comment '消息类型：user/ai',
    content     text                               not null comment '消息内容',
    tokens      int      default 0                 not null comment '消息token数',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_userId (userId),
    index idx_aiAvatarId (aiAvatarId),
    index idx_sessionId (sessionId)
) comment 'AI分身对话历史' collate = utf8mb4_unicode_ci;

-- 用户反馈表
create table if not exists user_feedback
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                             not null comment '用户ID',
    feedbackType varchar(64)                       not null comment '反馈类型',
    title       varchar(256)                       null comment '反馈标题',
    content     text                               not null comment '反馈内容',
    attachment  varchar(1024)                      null comment '附件URL',
    status      tinyint  default 0                 not null comment '处理状态：0-待处理，1-处理中，2-已处理',
    adminId     bigint                             null comment '处理管理员ID',
    processTime datetime                           null comment '处理时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId),
    index idx_status (status)
) comment '用户反馈' collate = utf8mb4_unicode_ci;

CREATE TABLE `user_feedback_reply`
(
    id          bigint        NOT NULL AUTO_INCREMENT COMMENT 'id',
    feedbackId  bigint        NOT NULL COMMENT '关联的反馈ID',
    senderId    bigint        NOT NULL COMMENT '发送者ID',
    senderRole  tinyint       NOT NULL COMMENT '发送者角色：0-用户，1-管理员',
    content     varchar(1000) NOT NULL COMMENT '回复内容',
    attachment  varchar(1024)          DEFAULT NULL COMMENT '附件URL',
    isRead      tinyint       NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
    createTime  datetime      NOT NULL COMMENT '创建时间',
    updateTime  datetime      NOT NULL COMMENT '更新时间',
    isDelete    tinyint       NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (id),
    KEY idx_feedback_id (feedbackId),
    KEY idx_sender_id (senderId),
    KEY idx_create_time (createTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈回复';

-- 用户生词本表
CREATE TABLE IF NOT EXISTS user_word_book
(
    id             BIGINT AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    userId         BIGINT                             NOT NULL COMMENT '用户id，关联到user表',
    wordId         BIGINT                             NOT NULL COMMENT '单词id，关联到daily_word表',
    learningStatus TINYINT  DEFAULT 0                 NOT NULL COMMENT '学习状态：0-未学习，1-已学习，2-已掌握',
    isCollected    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否收藏：0-否，1-是',
    collectedTime  DATETIME                           NULL COMMENT '收藏时间',
    difficulty     TINYINT  DEFAULT 1                 NOT NULL COMMENT '难度等级：1-简单，2-中等，3-困难',
    isDeleted      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_wordId (wordId),
    INDEX idx_learningStatus (learningStatus),
    INDEX idx_isCollected (isCollected),
    INDEX idx_difficulty (difficulty),
    UNIQUE uk_user_word (userId, wordId)
) COMMENT '用户生词本' COLLATE = utf8mb4_unicode_ci;

-- 班级信息表
CREATE TABLE IF NOT EXISTS class_info
(
    id          BIGINT AUTO_INCREMENT COMMENT '班级ID' PRIMARY KEY,
    className   VARCHAR(256)                       NOT NULL COMMENT '班级名称',
    description TEXT                               NULL COMMENT '班级描述',
    creatorId   BIGINT                             NOT NULL COMMENT '创建者ID，关联到user表',
    createTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_creatorId (creatorId),
    CONSTRAINT fk_class_info_creator FOREIGN KEY (creatorId) REFERENCES user(id)
) COMMENT '班级信息' COLLATE = utf8mb4_unicode_ci;

-- 班级成员表
CREATE TABLE IF NOT EXISTS class_member
(
    id         BIGINT AUTO_INCREMENT COMMENT '成员ID' PRIMARY KEY,
    classId    BIGINT                             NOT NULL COMMENT '班级ID，关联到class_info表',
    userId     BIGINT                             NOT NULL COMMENT '用户ID，关联到user表',
    role       VARCHAR(32)                        NOT NULL COMMENT '角色，如teacher/student',
    joinTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '加入时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_classId (classId),
    INDEX idx_userId (userId),
    UNIQUE uk_class_user (classId, userId),
    CONSTRAINT fk_class_member_class FOREIGN KEY (classId) REFERENCES class_info(id),
    CONSTRAINT fk_class_member_user FOREIGN KEY (userId) REFERENCES user(id)
) COMMENT '班级成员' COLLATE = utf8mb4_unicode_ci;

-- 班级与课程关联表
CREATE TABLE IF NOT EXISTS class_course
(
    id         BIGINT AUTO_INCREMENT COMMENT '关联ID' PRIMARY KEY,
    classId    BIGINT                             NOT NULL COMMENT '班级ID，关联到class_info表',
    courseId   BIGINT                             NOT NULL COMMENT '课程ID，关联到course表',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_classId (classId),
    INDEX idx_courseId (courseId),
    UNIQUE uk_class_course (classId, courseId),
    CONSTRAINT fk_class_course_class FOREIGN KEY (classId) REFERENCES class_info(id),
    CONSTRAINT fk_class_course_course FOREIGN KEY (courseId) REFERENCES course(id)
) COMMENT '班级与课程关联' COLLATE = utf8mb4_unicode_ci;

-- 作业表
CREATE TABLE IF NOT EXISTS class_assignment
(
    id          BIGINT AUTO_INCREMENT COMMENT '作业ID' PRIMARY KEY,
    classId     BIGINT                             NOT NULL COMMENT '班级ID，关联到class_info表',
    courseId    BIGINT                             NOT NULL COMMENT '课程ID，关联到course表',
    title       VARCHAR(256)                       NOT NULL COMMENT '作业标题',
    description TEXT                               NULL COMMENT '作业描述',
    deadline    DATETIME                           NULL COMMENT '截止日期',
    creatorId   BIGINT                             NOT NULL COMMENT '创建者ID，关联到user表',
    createTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_classId (classId),
    INDEX idx_courseId (courseId),
    INDEX idx_creatorId (creatorId),
    CONSTRAINT fk_class_assignment_class FOREIGN KEY (classId) REFERENCES class_info(id),
    CONSTRAINT fk_class_assignment_course FOREIGN KEY (courseId) REFERENCES course(id),
    CONSTRAINT fk_class_assignment_creator FOREIGN KEY (creatorId) REFERENCES user(id)
) COMMENT '班级作业' COLLATE = utf8mb4_unicode_ci;

-- 作业提交表
CREATE TABLE IF NOT EXISTS assignment_submission
(
    id            BIGINT AUTO_INCREMENT COMMENT '提交ID' PRIMARY KEY,
    assignmentId  BIGINT                             NOT NULL COMMENT '作业ID，关联到class_assignment表',
    userId        BIGINT                             NOT NULL COMMENT '提交者ID，关联到user表',
    content       TEXT                               NULL COMMENT '提交内容',
    attachmentUrl VARCHAR(1024)                      NULL COMMENT '附件URL',
    submitTime    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '提交时间',
    score         DECIMAL(5,2)                       NULL COMMENT '评分',
    feedback      TEXT                               NULL COMMENT '教师反馈',
    isDelete      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_assignmentId (assignmentId),
    INDEX idx_userId (userId),
    UNIQUE uk_submission (assignmentId, userId),
    CONSTRAINT fk_assignment_submission_assignment FOREIGN KEY (assignmentId) REFERENCES class_assignment(id),
    CONSTRAINT fk_assignment_submission_user FOREIGN KEY (userId) REFERENCES user(id)
) COMMENT '作业提交' COLLATE = utf8mb4_unicode_ci;

-- 私聊消息表
CREATE TABLE IF NOT EXISTS private_message
(
    id          BIGINT AUTO_INCREMENT COMMENT '消息ID' PRIMARY KEY,
    senderId    BIGINT                             NOT NULL COMMENT '发送者ID，关联到user表',
    receiverId  BIGINT                             NOT NULL COMMENT '接收者ID，关联到user表',
    content     TEXT                               NOT NULL COMMENT '消息内容',
    isRead      TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否已读：0-否，1-是',
    createTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '发送时间',
    isDelete    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_senderId (senderId),
    INDEX idx_receiverId (receiverId),
    INDEX idx_createTime (createTime),
    CONSTRAINT fk_private_message_sender FOREIGN KEY (senderId) REFERENCES user(id),
    CONSTRAINT fk_private_message_receiver FOREIGN KEY (receiverId) REFERENCES user(id)
) COMMENT '私聊消息' COLLATE = utf8mb4_unicode_ci;

-- 私聊会话表
CREATE TABLE IF NOT EXISTS private_chat_session
(
    id              BIGINT AUTO_INCREMENT COMMENT '会话ID' PRIMARY KEY,
    userId1         BIGINT                             NOT NULL COMMENT '用户1 ID，关联到user表',
    userId2         BIGINT                             NOT NULL COMMENT '用户2 ID，关联到user表',
    lastMessageTime DATETIME                           NULL COMMENT '最后一条消息时间',
    createTime      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete        TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除：0-否，1-是',
    INDEX idx_userId1 (userId1),
    INDEX idx_userId2 (userId2),
    UNIQUE uk_session (userId1, userId2),
    CONSTRAINT fk_private_chat_session_user1 FOREIGN KEY (userId1) REFERENCES user(id),
    CONSTRAINT fk_private_chat_session_user2 FOREIGN KEY (userId2) REFERENCES user(id)
) COMMENT '私聊会话' COLLATE = utf8mb4_unicode_ci;

-- 好友关系表
CREATE TABLE IF NOT EXISTS friend_relationship
(
    id         BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    userId1    BIGINT                             NOT NULL COMMENT '用户1 ID，关联到user表',
    userId2    BIGINT                             NOT NULL COMMENT '用户2 ID，关联到user表',
    status     VARCHAR(20)                        NOT NULL COMMENT '关系状态：pending/accepted/blocked',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_userId1 (userId1),
    INDEX idx_userId2 (userId2),
    UNIQUE uk_friend (userId1, userId2),
    CONSTRAINT fk_friend_user1 FOREIGN KEY (userId1) REFERENCES user(id),
    CONSTRAINT fk_friend_user2 FOREIGN KEY (userId2) REFERENCES user(id)
) COMMENT '好友关系表' COLLATE = utf8mb4_unicode_ci;

-- 好友申请表
CREATE TABLE IF NOT EXISTS friend_request
(
    id         BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    senderId   BIGINT                             NOT NULL COMMENT '发送者 ID，关联到user表',
    receiverId BIGINT                             NOT NULL COMMENT '接收者 ID，关联到user表',
    status     VARCHAR(20)                        NOT NULL COMMENT '申请状态：pending/accepted/rejected',
    message    VARCHAR(512)                       NULL COMMENT '申请消息',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_senderId (senderId),
    INDEX idx_receiverId (receiverId),
    UNIQUE uk_request (senderId, receiverId),
    CONSTRAINT fk_request_sender FOREIGN KEY (senderId) REFERENCES user(id),
    CONSTRAINT fk_request_receiver FOREIGN KEY (receiverId) REFERENCES user(id)
) COMMENT '好友申请表' COLLATE = utf8mb4_unicode_ci;
```









## 十一、数据库类图

![img](https://cdn.nlark.com/yuque/0/2025/jpeg/50745479/1741669557036-1d92cd1b-9e21-426a-8f20-0f993de6cb85.jpeg)

------
