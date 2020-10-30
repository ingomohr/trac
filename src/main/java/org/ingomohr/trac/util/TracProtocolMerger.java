package org.ingomohr.trac.util;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import org.ingomohr.trac.model.TracItem;
import org.ingomohr.trac.model.TracProtocol;
import org.ingomohr.trac.model.TracTopic;

/**
 * Merges a {@link TracProtocol} onto another {@link TracProtocol}.
 * <p>
 * Clients set the protocol to be updated and then call
 * {@link #merge(TracProtocol)} with the protocol they want to merge upon the
 * protocol that was set before.
 * </p>
 * <p>
 * Merging protocols merges the topics and keeps the items. i.e. if both
 * protocols have the same topic t, the protocol merged onto will have that
 * topic t, and it will have that topic with all items from the first protocol
 * and all items from the second protocol. The items as such are not modified
 * other than the topic-object linking.
 * </p>
 */
public class TracProtocolMerger {

    private TracProtocol protocol;

    public TracProtocolMerger() {
    }

    /**
     * Creates a new merger.
     * 
     * @param protocol the protocol that is to be updated by the merger.
     */
    public TracProtocolMerger(TracProtocol protocol) {
        setProtocol(protocol);
    }

    /**
     * Merges the given protocol onto the protocol that is accessible via
     * {@link #getProtocol()}.
     * <p>
     * The given protocol will not be modified.
     * </p>
     * 
     * @param protocol the protocol that is to be merged onto the protocol
     *                 accessible via {@link #getProtocol()}. Cannot be
     *                 <code>null</code>.
     */
    public void merge(TracProtocol protocol) {

        requireNonNull(protocol);

        if (!protocol.getTopics().isEmpty()) {
            merge(protocol.getTopics(), getProtocol().getTopics());
        }

        for (TracItem item : protocol.getItems()) {
            getProtocol().getItems().add(item);
        }
    }

    private void merge(List<TracTopic> topicsToMerge, List<TracTopic> topicsToMergeOnto) {
        for (TracTopic topic : topicsToMerge) {

            TracTopic matchingTopic = topicsToMergeOnto.stream()
                    .filter(t -> Objects.equals(t.getName(), topic.getName())).findFirst().orElse(null);

            if (matchingTopic == null) {
                topicsToMergeOnto.add(topic);
            } else {
                for (TracTopic topicChild : topic.getChildren()) {
                    matchingTopic.getChildren().add(topicChild);
                    topicChild.setParent(matchingTopic);
                }
            }
        }
    }

    /**
     * Returns the protocol to be updated by the merger.
     * 
     * @return protocol. The protocol to update. <code>null</code> if not set
     *         before.
     */
    public TracProtocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol to be updated by the merger.
     * 
     * @param protocol the protocol to update.
     */
    public void setProtocol(TracProtocol protocol) {
        this.protocol = protocol;
    }

}
