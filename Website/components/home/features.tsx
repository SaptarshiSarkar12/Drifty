"use client";

export default function Features() {
  const features = [
    {
      icon: "📥",
      title: "Download from Any Site",
      desc: "Effortlessly download videos from leading platforms like YouTube and Instagram.",
    },
    {
      icon: "🚀",
      title: "High-Speed Downloads",
      desc: "Harnesses multi-threading technology for accelerated and efficient parallel downloads.",
    },
    {
      icon: "📂",
      title: "Multiple File Formats",
      desc: "Supports videos, images, documents, and more.",
    },
    {
      icon: "💻",
      title: "Lightweight & Efficient",
      desc: "A minimal yet powerful Java-based downloader.",
    },
    {
      icon: "🔄",
      title: "Batch Download Support",
      desc: "Download multiple files simultaneously with ease.",
    },
    {
      icon: "🖥️",
      title: "GUI & CLI Modes",
      desc: "Drifty's CLI and GUI modes provide adaptable, streamlined user experiences across diverse scenarios.",
    },
  ];

  return (
    <section className="py-24" aria-label="Features Section">
      <div className="max-w-6xl mx-auto px-8 text-center">
        <h2 className="text-5xl font-extrabold tracking-tight">
          Why Choose Drifty?
        </h2>
        <p className="mt-5 text-lg max-w-2xl mx-auto">
          A powerful Java-based downloader that supports all websites and social
          media.
        </p>

        <div className="mt-16 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="p-8 dark:border rounded-xl shadow-lg hover:shadow-2xl transition-all duration-300 ease-in-out transform hover:scale-95 dark:hover:border-neutral-600"
            >
              <div className="text-5xl mb-4" aria-hidden="true">
                {feature.icon}
              </div>
              <h3 className="text-2xl font-semibold leading-snug">
                {feature.title}
              </h3>
              <p className="mt-3 text-base leading-relaxed">{feature.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
