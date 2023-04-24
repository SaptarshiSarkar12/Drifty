import '/styles/globals.css'
import Head from 'next/Head'

export default function Head(){
	return (
		<Head>
        <title>Drifty</title>
        <meta name="description" content="Interactive File Downloader System" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="apple-touch-icon" sizes="180x180" href="favicons/apple-touch-icon.png" />
        <link rel="icon" type="image/png" sizes="32x32" href="favicons/favicon-32x32.png" />
        <link rel="icon" type="image/png" sizes="16x16" href="favicons/favicon-16x16.png" />
        <link rel="manifest" href="favicons/site.webmanifest" />
        </Head>
	)
}

export default function App({ Component, pageProps }) {
  return <Component {...pageProps} />
}
