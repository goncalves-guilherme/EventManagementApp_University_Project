using SilentManagerApiServer.DataAccess;
using SilentManagerApiServer.Models.DB;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace SilentManager.API.DataAccess.Repositories
{
    public class PersonalSettingsRepository : Repository<PersonalSettings>, IPersonalSettingsRepository
    {
        private readonly SilentManagerContext dbContext;

        public PersonalSettingsRepository(SilentManagerContext dbContext) 
            : base (dbContext){
            this.dbContext = dbContext;
        }
    }
}
