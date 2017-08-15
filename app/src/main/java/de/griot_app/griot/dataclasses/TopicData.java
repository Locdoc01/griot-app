package de.griot_app.griot.dataclasses;

/**
 * Data holding class for standard topic data
 */
public class TopicData {

    private int topicKey;
    private String topic;

    //default-constructor
    public TopicData() {

    }

    @Override
    public String toString() {
        return "TopicData{" +
                "topicKeyn='" + topicKey + '\'' +
                ", topic=" + topic +
                '}';
    }

    //get-methods
    public String getTopic() { return topic; }

    public int getTopicKey() { return topicKey; }

    //set-methods
    public void setTopic(String topic) { this.topic = topic; }

    public void setTopicKey(int topicKey) { this.topicKey = topicKey; }
}
