package eu.kanade.tachiyomi.ui.reader

/**
 * Delegate for handling reader control interactions.
 * Used by ScrollTimer and other controls to interact with the reader.
 */
interface ReaderControlDelegate {

    /**
     * Listener for scroll/interaction events
     */
    interface OnInteractionListener {

        /**
         * Called when user interacts with the screen.
         * Temporarily pauses autoscroll.
         */
        fun onUserInteraction()

        /**
         * Check if reader is actively displayed/resumed
         */
        fun isReaderResumed(): Boolean

        /**
         * Scroll within current page
         * @param delta Amount to scroll (positive = down/right, negative = up/left)
         * @param smooth Whether to use smooth scrolling
         * @return true if scrolled, false if reached page boundary
         */
        fun scrollBy(delta: Int, smooth: Boolean): Boolean

        /**
         * Switch to next/previous page
         * @param delta Page delta (+1 = next, -1 = previous)
         */
        fun switchPageBy(delta: Int)
    }
}
