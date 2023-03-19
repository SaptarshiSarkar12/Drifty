import { Html, Head, Main, NextScript } from 'next/document'

export default function Document() {
  return (
    <Html lang="en" className='scroll-smooth' style={{scrollBehavior:'smooth'}}>
      <Head >
      <script src="https://kit.fontawesome.com/5c11ffe082.js" crossorigin="anonymous"></script>
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  )
}
