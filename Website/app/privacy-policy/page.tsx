export default async function PrivacyPolicy() {
  const response = await fetch(
    "https://raw.githubusercontent.com/SaptarshiSarkar12/Drifty/c86ce85731008f20ec5a67d423a5047d49ebc40d/Privacy%20Policy.txt"
  );
  const content = await response.text();

  return (
    <div className="max-w-3xl mx-auto py-10 px-2 sm:px-4 lg:px-6">
      <h1 className="text-3xl font-bold text-center mb-6">Privacy Policy</h1>
      <pre className="bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-200 p-4 rounded-lg whitespace-pre-wrap border border-gray-300 dark:border-gray-700 text-sm sm:text-base leading-relaxed overflow-x-auto">
        {content}
      </pre>
    </div>
  );
}
