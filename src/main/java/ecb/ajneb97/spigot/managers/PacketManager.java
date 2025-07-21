package ecb.ajneb97.spigot.managers;

/**
 * Common interface for packet managers that handle tab completion blocking
 * Supports both ProtocolLib and PacketEvents implementations
 */
public interface PacketManager {
    
    /**
     * Check if the packet manager is enabled and ready to use
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();
    
    /**
     * Get the name of the packet library being used
     * @return library name (e.g., "ProtocolLib", "PacketEvents")
     */
    String getLibraryName();
    
    /**
     * Terminate and clean up resources when plugin is disabled
     */
    void terminate();
}