using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.DataAccess.Repositories
{
    public class EventHasCategoriesRepository : Repository<EventHasCategories>, IEventHasCategoriesRepository
    {
        private readonly SilentManagerContext dbContext;

        public EventHasCategoriesRepository(SilentManagerContext dbContext) : base(dbContext)
        {
            this.dbContext = dbContext;
        }

    }
}
