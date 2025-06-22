interface DownloadsIframeProps {
  src: string;
}

export const DownloadsIframe: React.FC<DownloadsIframeProps> = ({ src }) => {
  return (
    <div
      style={{
        width: "100%", // Full width
        height: "auto",
        overflow: "hidden", // Hide overflow to prevent scrolling
        borderRadius: "10px", // Optional styling
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)", // Optional shadow
        position: "relative",
        aspectRatio: "16 / 9", // Ensures responsive height
      }}
    >
      <iframe
        src={src}
        style={{
          width: "160%", // Zoom out effect (tweak as needed)
          height: "150%",
          transform: "scale(0.67)", // Scale iframe to fit without scrolling
          transformOrigin: "top left",
          border: "none",
          pointerEvents: "none", // Prevent user interaction
          overflow: "hidden", // Ensure no scrollbar
          position: "absolute",
          top: "0",
          left: "0",
        }}
      ></iframe>
    </div>
  );
};
