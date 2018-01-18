<%--
~ Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved
--%>

<html>
<head>
    <title>Topical Taxonomy</title>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>

<body>
<%@ page import ="java.util.*" %>
<%@ page import ="com.skenzo.beans.*" %>
<%@ page import ="com.skenzo.utils.*" %>
<div class="container">
    <div class="jumbotron">
        <%
        String url = String.valueOf(request.getAttribute("url"));
        %>
        <h3>Topical Taxonomy</h3>
        <p>URL: <a href='<%= url %>' target="_blank"><%= url %></a></p>
    </div>

    <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" href="#urlBean">Url Info</a></li>
        <li><a data-toggle="tab" href="#candidateTopics">Candidate Topics</a></li>
        <li><a data-toggle="tab" href="#finalCandidateKeywords">Final Candidate Keywords</a></li>
        <li><a data-toggle="tab" href="#keywordsForClustering">Clustering Keywords</a></li>
        <li><a data-toggle="tab" href="#clusters">Clusters</a></li>
        <li><a data-toggle="tab" href="#finalResult">Final Result</a></li>
        <li><a data-toggle="tab" href="#deepKeywords">Deep Keywords</a></li>
        <li><a data-toggle="tab" href="#taxonomy">Taxonomy</a></li>
    </ul>
    <%
    BeanBaggage keywordExtractorBean = (BeanBaggage)(request.getAttribute("keywordExtractorBean"));
    %>
    <div class="tab-content">
        <div id="urlBean" class="tab-pane fade in active">
            <%
            UrlBean urlBean = keywordExtractorBean.getUrlBean();
            %>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_canonical_hash">Canonical Hash</a>
                        </h4>
                    </div>
                    <div id="collapse_canonical_hash" class="panel-collapse collapse">
                        <div class="panel-body"><%= urlBean.getUrlCanonicalHash() %></div>
                    </div>
                </div>
            </div>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_title">Title</a>
                        </h4>
                    </div>
                    <div id="collapse_title" class="panel-collapse collapse">
                        <div class="panel-body"><%= urlBean.getTitle() %></div>
                    </div>
                </div>
            </div>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_meta">Meta Content</a>
                        </h4>
                    </div>
                    <div id="collapse_meta" class="panel-collapse collapse">
                        <div class="panel-body"><%= urlBean.getMetaContent() %></div>

                    </div>
                </div>
            </div>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_useful_content">Useful Content</a>
                        </h4>
                    </div>
                    <div id="collapse_useful_content" class="panel-collapse collapse">
                        <div class="panel-body"><%= urlBean.getUsefulContent() %></div>

                    </div>
                </div>
            </div>
        </div>
        <%
        CandidateKeywordsBean candidateKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.NonGlobal);
        CandidateKeywordsBean candidateGlobalKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.Global);
        CandidateKeywordsBean candidatePremiumKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.Premium);
        CandidateKeywordsBean candidateLearnedKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.TopAndExploreLearned);
        CandidateKeywordsBean candidateCrapKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.CrapLearned);
        CandidateKeywordsBean candidateGlobalLearnedKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.TopAndExploreGlobalLearned);
        CandidateKeywordsBean candidateGlobalCrapKeywordsBean = keywordExtractorBean.getCandidateKeywordsBean(CandidateKeywordsBeanType.CrapGlobalLearned);
        %>
        <div id="candidateTopics" class="tab-pane fade">
            <%
            CandidateTopicsBean candidateTopicsBean = keywordExtractorBean.getCandidateTopicsBean();
            %>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#title_topics">Title Topics</a>
                        </h4>
                    </div>
                    <div id="title_topics" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<TopicBean> titleTopics = (List<TopicBean>) candidateTopicsBean.getTitleTopics();
                            for (TopicBean topic : titleTopics){
                            String topicTitleWithCount = topic.getPageTitle() + "(" + candidateKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidatePremiumKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + ")";
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"> <%= topicTitleWithCount %>  </h4>
                                <span class="badge"> <%= topic.getScore() %> </span>
                                <p class="list-group-item-text">(<%= topic.getPageId() %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#content_topics">Content Topics</a>
                        </h4>
                    </div>
                    <div id="content_topics" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<TopicBean> contentTopics = (List<TopicBean>) candidateTopicsBean.getContentTopics();
                            for (TopicBean topic : contentTopics){
                            String topicTitleWithCount = topic.getPageTitle() + "(" + candidateKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidatePremiumKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + ")";
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"> <%= topicTitleWithCount %> </h4>
                                <span class="badge"> <%= topic.getScore() %> </span>
                                <p class="list-group-item-text">(<%= topic.getPageId() %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#meta_content_topics">Meta Content Topics</a>
                        </h4>
                    </div>
                    <div id="meta_content_topics" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<TopicBean> metaContentTopics = (List<TopicBean>) candidateTopicsBean.getMetaContentTopics();
                            for (TopicBean topic : metaContentTopics){
                            String topicTitleWithCount = topic.getPageTitle() + "(" + candidateKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidatePremiumKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + ")";
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"> <%= topicTitleWithCount %> </h4>
                                <span class="badge"> <%= topic.getScore() %> </span>
                                <p class="list-group-item-text">(<%= topic.getPageId() %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#final_topics">Final Topics Considered</a>
                        </h4>
                    </div>
                    <div id="final_topics" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<TopicBean> finalTopics = (List<TopicBean>) candidateTopicsBean.getFinalTopicsToConsider();
                            for (TopicBean topic : finalTopics){
                            String topicTitleWithCount = topic.getPageTitle() + "(" + candidateKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidatePremiumKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalLearnedKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + "," + candidateGlobalCrapKeywordsBean.getCountOfKeywordForTopic(topic.getPageId())
                            + ")";
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"> <%= topicTitleWithCount %> </h4>
                                <span class="badge"> <%= topic.getScore() %> </span>
                                <p class="list-group-item-text">(<%= topic.getPageId() %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div id="finalCandidateKeywords" class="tab-pane fade">

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#non_global_candidate_keywords">Non Global Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="non_global_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#global_candidate_keywords">Global Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="global_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateGlobalKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateGlobalKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#premium_candidate_keywords">Premium Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="premium_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidatePremiumKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidatePremiumKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#learned_candidate_keywords">Top And Explore Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="learned_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateLearnedKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateLearnedKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#crap_candidate_keywords">Crap Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="crap_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateCrapKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateCrapKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#global_learned_candidate_keywords">Global Top And Explore Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="global_learned_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateGlobalLearnedKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateGlobalLearnedKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#global_crap_candidate_keywords">Global Crap Candidate Keywords</a>
                        </h4>
                    </div>
                    <div id="global_crap_candidate_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            Util.sortOnCosine(candidateGlobalCrapKeywordsBean.getFinalKeywordList());
                            for (KeywordBean keyword : candidateGlobalCrapKeywordsBean.getFinalKeywordList()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

        </div>
        <%
        ClusterBean nonLearnedClusterBean = keywordExtractorBean.getClusterBean(ResultType.NonLearned);
        ClusterBean learnedClusterBean = keywordExtractorBean.getClusterBean(ResultType.Learned);
        %>

        <div id="keywordsForClustering" class="tab-pane fade">

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#non_learned_Keywords_for_clustering">Non Learned Keywords For Clustering</a>
                        </h4>
                    </div>
                    <div id="non_learned_Keywords_for_clustering" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<KeywordBean> clusteredKeywords = new ArrayList<KeywordBean>();
                            for (Map.Entry<String, KeywordBean> entry : nonLearnedClusterBean.getKeywordsForClustering().entrySet()){
                            clusteredKeywords.add(entry.getValue());
                            }
                            Util.sortOnCosine(clusteredKeywords);
                            for (KeywordBean keyword : clusteredKeywords){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#learned_Keywords_for_clustering">Learned Keywords For Clustering</a>
                        </h4>
                    </div>
                    <div id="learned_Keywords_for_clustering" class="panel-collapse collapse">
                        <ul>
                            <%
                            List<KeywordBean> learnedClusteredKeywords = new ArrayList<KeywordBean>();
                            for (Map.Entry<String, KeywordBean> entry : learnedClusterBean.getKeywordsForClustering().entrySet()){
                            learnedClusteredKeywords.add(entry.getValue());
                            }
                            Util.sortOnCosine(learnedClusteredKeywords);
                            for (KeywordBean keyword : learnedClusteredKeywords){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>



        </div>
        <div id="clusters" class="tab-pane fade">

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#non_learned_clusters">Non Learned Clusters</a>
                        </h4>
                    </div>
                    <div id="non_learned_clusters" class="panel-collapse collapse">
                        <ul>
                            <%
                            int cluster_index = 1;
                            for(List<KeywordBean> singleCluster : nonLearnedClusterBean.getClusters()){
                            %>
                            <div class="panel-group">
                                <div class="panel panel-default">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_cluster_<%=cluster_index%>">Cluster <%= cluster_index %></a>
                                        </h4>
                                    </div>
                                    <div id="collapse_cluster_<%=cluster_index%>" class="panel-collapse collapse">
                                        <ul>
                                            <%
                                            for (KeywordBean keyword : singleCluster){
                                            String keywordAsString = keyword.toString();
                                            %>
                                            <li class="list-group-item list-group-item-success">
                                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                                            </li>
                                            <%    }  %>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <% cluster_index = cluster_index + 1; } %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#learned_clusters">Learned Clusters</a>
                        </h4>
                    </div>
                    <div id="learned_clusters" class="panel-collapse collapse">
                        <ul>
                            <%
                            cluster_index = 1;
                            for(List<KeywordBean> singleCluster : learnedClusterBean.getClusters()){
                            %>
                            <div class="panel-group">
                                <div class="panel panel-default">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a style="color:#00008B" data-toggle="collapse" href="#learned_collapse_cluster_<%=cluster_index%>">Cluster <%= cluster_index %></a>
                                        </h4>
                                    </div>
                                    <div id="learned_collapse_cluster_<%=cluster_index%>" class="panel-collapse collapse">
                                        <ul>
                                            <%
                                            for (KeywordBean keyword : singleCluster){
                                            String keywordAsString = keyword.toString();
                                            %>
                                            <li class="list-group-item list-group-item-success">
                                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                                            </li>
                                            <%    }  %>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <% cluster_index = cluster_index + 1; } %>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div id="finalResult" class="tab-pane fade">

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#non_learned_Keywords_final_result">Non Learned Keywords</a>
                        </h4>
                    </div>
                    <div id="non_learned_Keywords_final_result" class="panel-collapse collapse">
                        <ul>
                            <%
                            for (KeywordBean keyword : nonLearnedClusterBean.getCentroids()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#learned_Keywords_final_result">Learned Keywords</a>
                        </h4>
                    </div>
                    <div id="learned_Keywords_final_result" class="panel-collapse collapse">
                        <ul>
                            <%
                            for (KeywordBean keyword : learnedClusterBean.getCentroids()){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>


        </div>
        <div id="deepKeywords" class="tab-pane fade">
            <%
            List<KeywordBean> deepNonLearnedKeywords = keywordExtractorBean.getDeepKeywords(ResultType.NonLearned);
            %>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_deep_keywords">Deep Non Learned Keywords</a>
                        </h4>
                    </div>
                    <div id="collapse_deep_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            for (KeywordBean keyword : deepNonLearnedKeywords){
                            String keywordAsString = keyword.toString();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>

            <%
            List<KeywordBean> deepLearnedKeywords = keywordExtractorBean.getDeepKeywords(ResultType.Learned);
                %>
                <div class="panel-group">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4 class="panel-title">
                                <a style="color:#00008B" data-toggle="collapse" href="#collapse_deep_learned_keywords">Deep Learned Keywords</a>
                            </h4>
                        </div>
                        <div id="collapse_deep_learned_keywords" class="panel-collapse collapse">
                            <ul>
                                <%
                                for (KeywordBean keyword : deepLearnedKeywords){
                                String keywordAsString = keyword.toString();
                                %>
                                <li class="list-group-item list-group-item-success">
                                    <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                    <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                    <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                                </li>
                                <%    }  %>
                            </ul>
                        </div>
                    </div>
                </div>

        <div id="taxonomy" class="tab-pane fade">
            <%
            List<KeywordBean> nonLearnedKeywords = (List<KeywordBean>)(request.getAttribute("taxonomyNonLearnedKeywords"));
            %>
            <div class="panel-group">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4 class="panel-title">
                            <a style="color:#00008B" data-toggle="collapse" href="#collapse_non_learned_keywords">Non Learned Keywords</a>
                        </h4>
                    </div>
                    <div id="collapse_non_learned_keywords" class="panel-collapse collapse">
                        <ul>
                            <%
                            for (KeywordBean keyword : nonLearnedKeywords){
                            String keywordAsString = "Rank : " + keyword.getRank()  + " , CPC : " + keyword.getTaxoCPC();
                            %>
                            <li class="list-group-item list-group-item-success">
                                <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                            </li>
                            <%    }  %>
                        </ul>
                    </div>
                </div>
            </div>
            <%
            List<KeywordBean> learnedKeywords = (List<KeywordBean>)(request.getAttribute("taxonomyLearnedKeywords"));
                %>
                <div class="panel-group">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h4 class="panel-title">
                                <a style="color:#00008B" data-toggle="collapse" href="#collapse_learned_keywords">Learned Keywords</a>
                            </h4>
                        </div>
                        <div id="collapse_learned_keywords" class="panel-collapse collapse">
                            <ul>
                                <%
                                for (KeywordBean keyword : learnedKeywords){
                                String keywordAsString = "Rank : " + keyword.getRank()  + " , CPC : " + keyword.getTaxoCPC();
                                %>
                                <li class="list-group-item list-group-item-success">
                                    <h4 class="list-group-item-heading"><%= keyword.getTerm() %> (<%= keyword.getId()%>)</h4>
                                    <span class="badge"> <%= keyword.getCosineScore() %> </span>
                                    <p class="list-group-item-text">(<%= keywordAsString %>)</p>
                                </li>
                                <%    }  %>
                            </ul>
                        </div>
                    </div>
                </div>
        </div>
    </div>
</body>
</html>