import { NewsAnalysisPage } from './app.po';

describe('news-analysis App', function() {
  let page: NewsAnalysisPage;

  beforeEach(() => {
    page = new NewsAnalysisPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
