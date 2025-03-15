import Link from "next/link";

export default function FAQs() {
  return (
    <div className="p-6 rounded-lg shadow-lg">
      <h2 className="text-2xl font-semibold text-gray-800 dark:text-white mb-4">
        FAQs & Documentation
      </h2>
      <p className="text-gray-600 dark:text-gray-300 mb-4">
        Check out our documentation for more information about Drifty.
      </p>
      <Link
        href="#"
        className="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300"
      >
        Visit Documentation →
      </Link>
      <p className="text-red-500 dark:text-red-400 italic text-sm">
        ☝️Page not accessible, currently{" "}
        <a
          href="https://github.com/SaptarshiSarkar12/Drifty/issues/273"
          className="underline-offset-2 underline hover:underline-offset-4 "
        >
          under development
        </a>
        .
      </p>
    </div>
  );
}
