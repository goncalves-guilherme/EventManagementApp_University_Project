using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManagerApiServer.BusinessLogic.Dtos
{
    public class PagedResult<T>
    {
        public int CurrentPage { get; }
        public int PageCount { get; }
        public int PageSize { get; }

        public IEnumerable<T> Results { get; }

        public PagedResult(int CurrentPage, int PageCount, int PageSize, IEnumerable<T> Results)
        {
            this.CurrentPage = CurrentPage;
            this.PageCount = PageCount;
            this.PageSize = PageSize;
            this.Results = Results;
        }
    }
}
