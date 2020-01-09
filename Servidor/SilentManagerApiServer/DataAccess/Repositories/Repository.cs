using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Threading.Tasks;

namespace SilentManagerApiServer.DataAccess
{
    public class Repository<TEntity> : IRepository<TEntity> where TEntity : class
    {
        protected readonly DbContext Context;

        public Repository(DbContext context)
        {
            this.Context = context;    
        }

        public void Add(TEntity entity)
        {
            Context.Add(entity);
        }

        public async void AddAsync(TEntity entity)
        {
            await Context.AddAsync(entity);
        }

        public void AddRange(IEnumerable<TEntity> entities)
        {
            Context.AddRange(entities);
        }

        public async void AddRangeAsync(IEnumerable<TEntity> entities)
        {
            await Context.AddRangeAsync(entities);
        }

        public IQueryable<TEntity> Find(Expression<Func<TEntity, bool>> predicate)
        {
            return Context.Set<TEntity>().Where(predicate);
        }

        public TEntity Get(int id)
        {
            return Context.Set<TEntity>().Find(id);
        }

        public async Task<TEntity> GetAsync(int id)
        {
            return await Context.Set<TEntity>().FindAsync(id);
        }

        public IEnumerable<TEntity> GetAll()
        {
            return Context.Set<TEntity>().ToList();
        }

        public async Task<IEnumerable<TEntity>> GetAllAsync()
        {
            return await Context.Set<TEntity>().ToListAsync();
        }

        public void Remove(TEntity entity)
        {
            Context.Remove(entity);
        }

        public void RemoveRange(IEnumerable<TEntity> entities)
        {
            Context.RemoveRange(entities);
        }

        public int Save()
        {
            return Context.SaveChanges();
        }

        public async Task<int> SaveAsync()
        {
            return await Context.SaveChangesAsync();
        }
    }
}
