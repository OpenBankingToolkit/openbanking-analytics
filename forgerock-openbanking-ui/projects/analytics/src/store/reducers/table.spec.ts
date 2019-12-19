import tableReducer, {
  DEFAULT_STATE,
  GetTablePageChangeAction,
  GetTableRequestAction,
  GetTableSuccessAction,
  GetTableErrorAction,
  GetTableFinishedAction,
  GetTableSortChangeAction,
  GetTableFilterChangeAction
} from './table';
import { ITPPSUsageAggregationResponse, UserType } from 'analytics/src/models';
import { SetDatesAction } from './dates';

describe('tableReducer', () => {
  it('should return the default state', () => {
    const action = {};
    const state = tableReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('should return the default state if action type does not match', () => {
    const action = { type: 'whatever' };
    const state = tableReducer(undefined, action);

    expect(state).toBe(DEFAULT_STATE);
  });

  it('GetTablePageChangeAction should set currentPage', () => {
    const id = 'id1';
    let page = 1;
    let state = tableReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = tableReducer(state, new GetTablePageChangeAction({ id, page }));

    expect(state[id].currentPage).toEqual(1);

    page++;

    state = tableReducer(state, new GetTablePageChangeAction({ id, page }));
    expect(state[id].currentPage).toEqual(2);
  });

  it('GetTableRequestAction should create the object', () => {
    const id = 'id1';
    let state = tableReducer(DEFAULT_STATE, {});

    expect(state[id]).toEqual(undefined);

    state = tableReducer(state, new GetTableRequestAction({ id }));

    expect(state[id]).toEqual({
      isFetching: true,
      error: ''
    });
  });

  it('GetTableSuccessAction should set ids and entities', () => {
    const id = 'id1';
    const size = 10;
    const page = 1;
    const totalPages = 10;
    const totalResults = 99;
    let state = tableReducer(DEFAULT_STATE, {});

    const response: ITPPSUsageAggregationResponse = {
      currentPage: 1,
      data: [
        {
          application: 'application1',
          count: 2,
          endpoint: 'test/haha',
          identityId: 'ergegerh984984erhg49e8r',
          method: 'GET',
          responseStatus: 'responseStatus',
          userType: UserType.ANONYMOUS
        }
      ],
      totalPages,
      totalResults
    };

    state = tableReducer(state, new GetTablePageChangeAction({ id, page, size }));

    state = tableReducer(state, new GetTableRequestAction({ id }));

    state = tableReducer(state, new GetTableSuccessAction({ id, response }));

    expect(state[id]).toEqual({
      isFetching: false,
      totalPages,
      totalResults,
      error: '',
      size,
      currentPage: page,
      pages: [undefined, response.data]
    });
  });

  it('GetTableErrorAction should set error', () => {
    const id = 'id1';
    const error = 'this is an error';
    let state = tableReducer(DEFAULT_STATE, {});

    state = tableReducer(state, new GetTableRequestAction({ id }));

    state = tableReducer(state, new GetTableErrorAction({ id, error }));

    expect(state[id]).toEqual({
      isFetching: false,
      error
    });
  });

  it('GetTableFinishedAction should set isFetching to false', () => {
    const id = 'id1';
    let state = tableReducer(DEFAULT_STATE, {});

    state = tableReducer(state, new GetTableRequestAction({ id }));

    expect(state[id].isFetching).toEqual(true);

    state = tableReducer(state, new GetTableFinishedAction({ id }));

    expect(state[id].isFetching).toEqual(false);
  });

  it('GetTableSortChangeAction should replace current sort', () => {
    const id = 'id1';
    let state = tableReducer(DEFAULT_STATE, {});

    state = tableReducer(
      state,
      new GetTableSortChangeAction({
        id,
        sort: {
          field: 'count',
          direction: 'DESC'
        }
      })
    );

    expect(state[id].sorts).toEqual([
      {
        field: 'count',
        direction: 'DESC'
      }
    ]);
  });

  it('GetTableFilterChangeAction should add, replace or remove filters', () => {
    const id = 'id1';
    let state = tableReducer(DEFAULT_STATE, {});

    // add
    state = tableReducer(
      state,
      new GetTableFilterChangeAction({
        id,
        filter: {
          field: 'tpp',
          regex: 'test'
        }
      })
    );

    expect(state[id].filters).toEqual([
      {
        field: 'tpp',
        regex: 'test'
      }
    ]);

    // replace
    state = tableReducer(
      state,
      new GetTableFilterChangeAction({
        id,
        filter: {
          field: 'tpp',
          regex: 'test2'
        }
      })
    );

    expect(state[id].filters).toEqual([
      {
        field: 'tpp',
        regex: 'test2'
      }
    ]);

    // add
    state = tableReducer(
      state,
      new GetTableFilterChangeAction({
        id,
        filter: {
          field: 'tpp2',
          regex: 'test'
        }
      })
    );

    expect(state[id].filters).toEqual([
      {
        field: 'tpp',
        regex: 'test2'
      },
      {
        field: 'tpp2',
        regex: 'test'
      }
    ]);
  });

  it('Changing sates via SetDatesAction should keep filters, sorts and size but delete data and reset pagination', () => {
    const id = 'id1';
    const size = 10;
    const page = 1;
    const totalPages = 10;
    const totalResults = 99;
    const sort = {
      field: 'count',
      direction: 'DESC'
    };
    const filter = {
      field: 'endpointType',
      regex: 'AISP'
    };
    let state = tableReducer(DEFAULT_STATE, {});

    const response: ITPPSUsageAggregationResponse = {
      currentPage: 1,
      data: [
        {
          application: 'application1',
          count: 2,
          endpoint: 'test/haha',
          identityId: 'ergegerh984984erhg49e8r',
          method: 'GET',
          responseStatus: 'responseStatus',
          userType: UserType.ANONYMOUS
        }
      ],
      totalPages,
      totalResults
    };

    state = tableReducer(state, new GetTablePageChangeAction({ id, page, size }));
    state = tableReducer(
      state,
      new GetTableSortChangeAction({
        id,
        sort
      })
    );
    state = tableReducer(
      state,
      new GetTableFilterChangeAction({
        id,
        filter
      })
    );

    state = tableReducer(state, new GetTableRequestAction({ id }));

    state = tableReducer(state, new GetTableSuccessAction({ id, response }));

    expect(state[id]).toEqual({
      isFetching: false,
      totalPages,
      totalResults,
      error: '',
      currentPage: page,
      pages: [undefined, response.data],
      size,
      sorts: [sort],
      filters: [filter]
    });

    const from = new Date().toString();
    const to = new Date().toString();

    state = tableReducer(state, new SetDatesAction({ from, to }));

    expect(state[id]).toEqual({
      isFetching: false,
      error: '',
      pages: [],
      currentPage: 0,
      totalPages: 0,
      totalResults: 0,
      // below values should be kept unchanged
      size,
      sorts: [sort],
      filters: [filter]
    });
  });
});
