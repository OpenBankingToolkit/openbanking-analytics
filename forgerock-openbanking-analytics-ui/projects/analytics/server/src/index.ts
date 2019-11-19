import * as express from 'express';
import * as cors from 'cors';
import * as cookieParser from 'cookie-parser';
import * as puppeteer from 'puppeteer';
import * as bodyParser from 'body-parser';
import * as uuid from 'uuid/v4';
import * as os from 'os';
import * as path from 'path';
import * as fs from 'fs';

async function run() {
  const {
    cookieDomain: COOKIE_DOMAIN = '.ui-integ.forgerock.financial',
    appAddress: ORIGIN = 'https://dev.analytics.ui-integ.forgerock.financial:4206'
  } = getDeploymentSetting();

  const PORT = process.env.PORT || 5000;
  const NODE_ENV = process.env.NODE_ENV;
  const app = express();

  app.use(cookieParser());
  app.use(bodyParser.json());
  app.use(
    cors({
      credentials: true,
      methods: 'GET, HEAD',
      origin: NODE_ENV === 'production' ? ORIGIN : '*'
    })
  );
  app.set('port', PORT);

  app.get('/config', (req: express.Request, res: express.Response) =>
    res.send({
      PORT,
      COOKIE_DOMAIN,
      NODE_ENV,
      ORIGIN
    })
  );

  app.post('/pdf', async (req: express.Request, res: express.Response, next: express.NextFunction) => {
    try {
      if (!req.body) {
        throw new Error('POST body missing');
      }

      const browser = await createBrowser();
      const sessionCookies = getSessionCookies(req.cookies, COOKIE_DOMAIN);
      const page = await createPage(
        browser,
        sessionCookies,
        `${ORIGIN}/pdf/?config=${encodeURIComponent(JSON.stringify(req.body))}`
      );
      const finalPDFPath = path.join(os.tmpdir(), `${uuid()}.pdf`);
      await page.pdf({
        format: 'A4',
        path: finalPDFPath,
        printBackground: true
      });
      await page.close();
      await browser.close();

      // force download and cleanup
      res.download(finalPDFPath, () => fs.unlinkSync(finalPDFPath));
    } catch (error) {
      next(error);
    }
  });

  app.get('/pdf', async (req: express.Request, res: express.Response, next: express.NextFunction) => {
    try {
      const { id } = req.query;
      if (!id) {
        throw new Error('id missing from query params');
      }

      const browser = await createBrowser();
      const sessionCookies = getSessionCookies(req.cookies, COOKIE_DOMAIN);
      const page = await createPage(browser, sessionCookies, `${ORIGIN}/pdf${encodeQueryData(req.query)}`);
      const finalPDFPath = path.join(os.tmpdir(), `${uuid()}.pdf`);
      await page.pdf({
        format: 'A4',
        path: finalPDFPath,
        printBackground: true
      });
      await page.close();
      await browser.close();

      // force download and cleanup
      res.download(finalPDFPath, () => fs.unlinkSync(finalPDFPath));
    } catch (error) {
      next(error);
    }
  });

  app.use(function(err: any, req: express.Request, res: express.Response, next: express.NextFunction) {
    res.status(500).json({
      error: err.toString()
    });
  });

  app.listen(app.get('port'), () => {
    console.log(`Web server listening on port ${app.get('port')}`);
  });
}

async function createBrowser() {
  return await puppeteer.launch({
    dumpio: process.env.NODE_ENV !== 'production',
    headless: true,
    ignoreHTTPSErrors: true,
    args: ['--no-sandbox', '--hide-scrollbars']
  });
}

async function createPage(browser: puppeteer.Browser, sessionCookies: puppeteer.SetCookie[], url: string) {
  const page = await browser.newPage();
  await page.setViewport({ width: 0, height: 0 });
  await page.setCookie(...sessionCookies);
  await page.goto(url, {
    waitUntil: 'networkidle0',
    timeout: 30000
  });
  // wait to make sure rendering is over
  await page.waitFor(500);
  return page;
}

function getDeploymentSetting(): { [key: string]: string } {
  try {
    return require(path.join(__dirname, 'deployment-settings.js'));
  } catch (error) {
    return {};
  }
}

function getSessionCookies(cookies: { [key: string]: string }, COOKIE_DOMAIN: string): puppeteer.SetCookie[] {
  return Object.keys(cookies)
    .filter((cookieKey: string) => ['obri-session'].includes(cookieKey))
    .map((name: string) => ({
      name,
      value: cookies[name],
      domain: COOKIE_DOMAIN
    }));
}

export function encodeQueryData(data: { [key: string]: any } = {}): string {
  const keys = Object.keys(data);
  if (!keys.length) return '';

  return (
    '?' +
    Object.keys(data)
      .map(function(key) {
        return [key, data[key]].map(encodeURIComponent).join('=');
      })
      .join('&')
  );
}

run();
