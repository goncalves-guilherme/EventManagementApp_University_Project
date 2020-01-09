using Microsoft.EntityFrameworkCore;
using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SilentManager.API.DataAccess.Repositories
{
    public class LocationRepository : Repository<Location>, ILocationRepository
    {
        public LocationRepository(DbContext context) : base(context)
        {
        }
    }
}
